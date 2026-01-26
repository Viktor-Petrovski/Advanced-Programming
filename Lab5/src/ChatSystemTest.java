import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class ChatSystemTest {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for (int i = 0; i < n; ++i) {
                k = jin.nextInt();
                if (k == 0) cr.addUser(jin.next());
                if (k == 1) cr.removeUser(jin.next());
                if (k == 2) System.out.println(cr.hasUser(jin.next()));
            }
            // System.out.println("");
            System.out.println(cr.toString());
            n = jin.nextInt();
            if (n == 0) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for (int i = 0; i < n; ++i) {
                k = jin.nextInt();
                if (k == 0) cr2.addUser(jin.next());
                if (k == 1) cr2.removeUser(jin.next());
                if (k == 2) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }
        if (k == 1) {
            ChatSystem cs = new ChatSystem();
            Method mts[] = cs.getClass().getMethods();
            while (true) {
                String cmd = jin.next();
                if (cmd.equals("stop")) break;
                if (cmd.equals("print")) {
                    System.out.println(cs.getRoom(jin.next()) + "\n");
                    continue;
                }
                for (Method m : mts) {
                    if (m.getName().equals(cmd)) {
                        String params[] = new String[m.getParameterTypes().length];
                        for (int i = 0; i < params.length; ++i) params[i] = jin.next();
                        m.invoke(cs, (Object[]) params);
                    }
                }
            }
        }
    }


    static class NoSuchRoomException extends Exception {
        public NoSuchRoomException(String message) {
            super(message);
        }
    }

    static class NoSuchUserException extends Exception {
        public NoSuchUserException(String message) {
            super(message);
        }
    }

    static class ChatRoom {
        private final String name;
        private final Set<String> users;

        public ChatRoom(String name) {
            this.name = name;
            users = new TreeSet<>();
        }

        public String getName() {
            return name;
        }

        public void addUser(String username) {
            users.add(username);
        }

        public void removeUser(String username) {
            users.remove(username);
        }

        public boolean hasUser(String username) {
            return users.contains(username);
        }

        public int numUsers() {
            return users.size();
        }

        @Override
        public String toString() {
            String isEmpty = users.isEmpty() ? "EMPTY\n" : "";
            StringBuilder sb = new StringBuilder();
            sb.append(name).append("\n").append(isEmpty);

            users.forEach(u -> sb.append(u).append("\n"));

            return sb.toString();
        }
    }

    static class ChatSystem {
        private final TreeMap<String, ChatRoom> rooms;
        private final TreeMap<String, TreeSet<ChatRoom>> users;

        public ChatSystem() {
            rooms = new TreeMap<>();
            users = new TreeMap<>();
        }

        public void addRoom(String roomName) {
            rooms.put(roomName, new ChatRoom(roomName));
        }

        public void removeRoom(String roomName) {
            rooms.remove(roomName);
        }

        public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
            if (!rooms.containsKey(roomName))
                throw new NoSuchRoomException(roomName);

            return rooms.get(roomName);
        }

        public void register(String userName) {
            users.put(userName, new TreeSet<>(Comparator.comparing(ChatRoom::getName)));

            rooms.values().stream().min(Comparator.comparing(ChatRoom::numUsers).thenComparing(ChatRoom::getName))
                    .ifPresent(r -> {
                        r.addUser(userName);
                        users.get(userName).add(r);
                    });
        }

        public void registerAndJoin(String userName, String roomName) throws NoSuchRoomException {
            users.put(userName, new TreeSet<>(Comparator.comparing(ChatRoom::getName)));
            joinRoom(userName, roomName);
        }

        public void joinRoom(String userName, String roomName) throws NoSuchRoomException {
            ChatRoom room = getRoom(roomName);

            room.addUser(userName);
            users.get(userName).add(room);
        }

        public void leaveRoom(String username, String roomName) throws NoSuchRoomException {
            ChatRoom room = getRoom(roomName);

            room.removeUser(username);
            users.get(username).remove(room);
        }

        public void followFriend(String username, String friend_username) throws NoSuchUserException {
            if (!users.containsKey(friend_username))
                throw new NoSuchUserException(friend_username);

            users.get(friend_username).forEach(r -> {
                r.addUser(username);
                users.get(username).add(r);
            });
        }
    }

}
