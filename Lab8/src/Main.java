import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    enum ActionType {
        JOIN_GAME,
        LEAVE_GAME,
        ATTACK
    }

    static class Player {
        private final String id;
        private int score;

        private final ReentrantLock lock;

        public Player(String id) {
            this.id = id;
            this.score = 0;

            lock = new ReentrantLock(true);
        }

        /// synchronized using a lock
        public void addScore(int delta) {
            lock.lock();
            try {
                score += delta;
            } finally {
                lock.unlock();
            }
        }


        @Override
        public String toString() {
            return "Player{" +
                    "id='" + id + '\'' +
                    ", score=" + score +
                    '}';
        }

    }

    static class PlayerAction {
        private final String playerId;
        private final ActionType action;

        public PlayerAction(String playerId, ActionType action) {
            this.playerId = playerId;
            this.action = action;
        }

        public String getPlayerId() {
            return playerId;
        }

        public ActionType getActionType() {
            return action;
        }

        public int getProcessingTime() {
            switch (action) {
                case JOIN_GAME:
                    return 20;
                case LEAVE_GAME:
                    return 30;
                case ATTACK:
                    return 5;
                default:
                    return 0;
            }
        }

        @Override
        public String toString() {
            return "PlayerAction{" +
                    "playerId='" + playerId + '\'' +
                    ", action=" + action +
                    '}';
        }
    }

    static class RoomAction {
        final String roomId;
        final PlayerAction action;

        RoomAction(String roomId, PlayerAction action) {
            this.roomId = roomId;
            this.action = action;
        }
    }


    static class GameRoom {
        public final String roomId;

        public final Map<String, Player> players = new ConcurrentHashMap<>();
        private final BlockingQueue<PlayerAction> actionQueue = new LinkedBlockingQueue<>();
        private final ExecutorService executor = Executors.newSingleThreadExecutor();

        public volatile boolean running = true;

        public GameRoom(String roomId) {
            this.roomId = roomId;
            startProcessor();
        }

        /// стартува посебен thread за процесирање на акциите. Овој thread:
        ///
        /// Континуирано чита акции од редицата
        /// Ги процесира додека собата е активна или додека има акции во редицата
        /// Користи poll() со timeout од 100ms за да не блокира бесконечно
        private void startProcessor() {
            executor.submit(() -> {
                while (running || !actionQueue.isEmpty()) {
                    try {
                        PlayerAction head = actionQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (head != null)
                            processAction(head);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        public void submitAction(PlayerAction action) {
            if (actionQueue.offer(action))
                System.out.println("[" + roomId + "] RECEIVED: " + action);
        }

        private void processAction(PlayerAction action) {
            try {
                Thread.sleep(action.getProcessingTime());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            switch (action.getActionType()) {
                case JOIN_GAME:
                    players.putIfAbsent(
                            action.getPlayerId(),
                            new Player(action.getPlayerId())
                    );
                    System.out.println("[" + roomId + "] JOIN: "
                            + action.getPlayerId());
                    break;

                case LEAVE_GAME:
                    if (players.remove(action.getPlayerId()) != null) {
                        System.out.println("[" + roomId + "] LEAVE: "
                                + action.getPlayerId());
                    } else {
                        System.out.println("[" + roomId
                                + "] LEAVE IGNORED (not in room): "
                                + action.getPlayerId());
                    }
                    break;

                case ATTACK:
                    Player p = players.get(action.getPlayerId());
                    if (p == null) {
                        System.out.println("[" + roomId
                                + "] ATTACK IGNORED (not in room): "
                                + action.getPlayerId());
                    } else {
                        p.addScore(10);
                        System.out.println("[" + roomId + "] ATTACK: " + p);
                    }
                    break;
            }
        }

        /// Го поставува флагот running на false
        /// Го исклучува executor-от (со grace period од 5 секунди)
        /// Ги печати финалните резултати на сите играчи во собата
        public void shutdown() {
            running = false;
            executor.shutdown();

            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS))
                    executor.shutdownNow();
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            System.out.println("[" + roomId + "] FINAL PLAYERS:");
            players.values().forEach(p -> System.out.println("  " + p));
        }
    }


    static class GameServer {

        private final BlockingQueue<RoomAction> inputQueue = new LinkedBlockingQueue<>();
        private final ConcurrentHashMap<String, GameRoom> rooms = new ConcurrentHashMap<>();
        private final ExecutorService dispatcher = Executors.newSingleThreadExecutor();

        private volatile boolean running = true;

        public GameServer() {
            startDispatcher();
        }

        /// Континуирано чита акции од главната редица
        /// Ја наоѓа соодветната соба (или ја креира ако не постои)
        /// Ја проследува акцијата до собата
        private void startDispatcher() {
            dispatcher.submit(() ->{
                while(running) {
                    try {
                        RoomAction ra = inputQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (ra != null) {
                            GameRoom room = rooms.computeIfAbsent(ra.roomId, GameRoom::new);
                            room.submitAction(ra.action);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });

        }

        public void submit(String roomId, PlayerAction action) {
            inputQueue.offer(new RoomAction(roomId, action));
        }

        /// Го исклучува dispatcher-от
        /// Ги исклучува сите активни игрички соби
        public void shutdown() throws InterruptedException {
            running = false;
            dispatcher.shutdown();
            if (!dispatcher.awaitTermination(5, TimeUnit.SECONDS))
                dispatcher.shutdownNow();

            for (GameRoom room : rooms.values())
                room.shutdown();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        GameServer server = new GameServer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {

            final String input = line.trim();

            try {
                String[] parts = input.split(",");
                if (parts.length != 3) {
                    System.err.println("Invalid input: " + input);
                    return;
                }

                String roomId = parts[0].trim();
                String playerId = parts[1].trim();
                ActionType actionType =
                        ActionType.valueOf(parts[2].trim());

                PlayerAction action =
                        new PlayerAction(playerId, actionType);

                server.submit(roomId, action);

            } catch (Exception e) {
                System.err.println(
                        "Failed to process line: " + input
                );
                System.out.println(e.getMessage());
            }
        }

        reader.close();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        server.shutdown();

        System.out.println("Game server stopped.");
    }
}
