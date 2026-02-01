import java.util.ArrayList;
import java.util.List;

public class PatternTest {
    public static void main(String[] args) {
        List<Song> listSongs = new ArrayList<>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);


        System.out.println(player);
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player);
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player);
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player);
    }

    static class Song {
        private final String title;
        private final String artist;

        Song(String title, String artist) {
            this.title = title;
            this.artist = artist;
        }

        @Override
        public String toString() {
            return "Song{" +
                    "title=" + title +
                    ", artist=" + artist +
                    '}';
        }
    }

    interface IState {
        IState play();

        IState stop();

        IState fwd();

        IState rwd();

        void printStatus();
    }

    static class Play implements IState {
        private final MP3Player player;
        private boolean isPlaying;

        Play(MP3Player player) {
            this.player = player;
            isPlaying = false;
        }

        @Override
        public IState play() {
            return this;
        }

        @Override
        public IState stop() {
            return new Paused(player);
        }

        @Override
        public IState fwd() {
            return new Forward(player).fwd();
        }

        @Override
        public IState rwd() {
            return new Rewind(player).rwd();
        }

        @Override
        public void printStatus() {
            if (isPlaying) System.out.println("Song is already playing");
            else System.out.printf("Song %d is playing\n", player.getCurrentSong());
            isPlaying = true;
        }
    }

    static class Paused implements IState {
        private final MP3Player player;

        Paused(MP3Player player) {
            this.player = player;
        }

        @Override
        public IState play() {
            return new Play(player);
        }

        @Override
        public IState stop() {
            return new Stop(player);
        }

        @Override
        public IState fwd() {
            return new Forward(player).fwd();
        }

        @Override
        public IState rwd() {
            return new Rewind(player).rwd();
        }

        @Override
        public void printStatus() {
            System.out.printf("Song %d is paused\n", player.getCurrentSong());
        }
    }

    static class Stop implements IState {
        private final MP3Player player;
        private boolean hasStopped;

        Stop(MP3Player player) {
            this.player = player;
            this.player.setCurrentSong(0);
        }

        @Override
        public IState play() {
            return new Play(player);
        }

        @Override
        public IState stop() {
            return this;
        }

        @Override
        public IState fwd() {
            return new Forward(player).fwd();
        }

        @Override
        public IState rwd() {
            return new Rewind(player).rwd();
        }

        @Override
        public void printStatus() {
            if (hasStopped)
                System.out.println("Songs are already stopped");
            else System.out.println("Songs are stopped");

            hasStopped = true;
        }
    }

    static class Forward implements IState {
        private final MP3Player player;

        Forward(MP3Player player) {
            this.player = player;
        }

        @Override
        public IState play() {
            return new Play(player);
        }

        @Override
        public IState stop() {
            return new Paused(player);
        }

        @Override
        public IState fwd() {
            int i = player.getCurrentSong();
            i = i + 1 == player.getSongs().size() ? 0 : i + 1;
            player.setCurrentSong(i);
            printStatus();
            return new Paused(player);
        }

        @Override
        public IState rwd() {
            return new Rewind(player).rwd(); // invalid operation
        }

        @Override
        public void printStatus() {
            System.out.println("Forward...");
        }
    }

    static class Rewind implements IState {
        private final MP3Player player;

        Rewind(MP3Player player) {
            this.player = player;
        }

        @Override
        public IState play() {
            return new Play(player);
        }

        @Override
        public IState stop() {
            return new Paused(player);
        }

        @Override
        public IState fwd() {
            return new Forward(player).fwd(); // invalid operation
        }

        @Override
        public IState rwd() {
            int i = player.getCurrentSong();
            i = i - 1 < 0 ? player.getSongs().size() - 1 : i - 1;
            player.setCurrentSong(i);
            printStatus();
            return new Paused(player);
        }

        @Override
        public void printStatus() {
            System.out.println("Reward...");
        }
    }

    static class MP3Player {
        private final List<Song> songs;
        private int currentSong;
        private IState state;
        
        MP3Player(List<Song> songs) {
            this.songs = songs;
            currentSong = 0;

            state = new Stop(this);
        }

        public int getCurrentSong() {
            return currentSong;
        }

        public void setCurrentSong(int currentSong) {
            this.currentSong = currentSong;
        }

        public List<Song> getSongs() {
            return songs;
        }

        private void printStatus() {
            state.printStatus();
        }

        void pressPlay() {
            state = state.play();
            printStatus();
        }

        void printCurrentSong() {
            System.out.println(songs.get(currentSong));
        }

        void pressFWD() {
            state = state.fwd();
        }

        void pressREW() {
            state = state.rwd();
        }

        void pressStop() {
            state = state.stop();
            printStatus();
        }

        @Override
        public String toString() {
            return "MP3Player{" +
                    "currentSong = " + currentSong +
                    ", songList = " + songs +
                    '}';
        }
    }
}
