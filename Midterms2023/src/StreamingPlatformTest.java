import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class StreamingPlatformTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StreamingPlatform sp = new StreamingPlatform();
        while (sc.hasNextLine()){
            String line = sc.nextLine();
            String [] parts = line.split(" ");
            String method = parts[0];
            String data = Arrays.stream(parts).skip(1).collect(Collectors.joining(" "));
            if (method.equals("addItem"))
                sp.addItem(data);
            else if (method.equals("listAllItems"))
                sp.listAllItems();
            else if (method.equals("listFromGenre")){
                System.out.println(data);
                sp.listFromGenre(data);
            }
        }

    }

    static abstract class Watchable implements Comparable<Watchable>{
        private final String title;
        private final List<String> genres;

        Watchable(String title, List<String> genres) {
            this.title = title;
            this.genres = genres;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getGenres() {
            return genres;
        }

        abstract double getAverageRating();

        @Override
        public int compareTo(Watchable o) {
            return Double.compare(o.getAverageRating(), this.getAverageRating());
        }
    }

    static class Movie extends Watchable {
        private final List<Integer> ratings;

        Movie(String title, List<String> genres, List<Integer> ratings) {
            super(title, genres);
            this.ratings = ratings;
        }

        @Override
        public double getAverageRating() {
            return ratings.stream().mapToDouble(i -> i).average().orElse(.0) *
                    Math.min(ratings.size() / 20.0, 1.0);
        }

        @Override
        public String toString() {
            return String.format("Movie %s %.4f", getTitle(), getAverageRating());
        }
    }

    static class Series extends Watchable {
        private final List<List<Integer>> episodes;

        Series(String title, List<String> genres, List<List<Integer>> episodes) {
            super(title, genres);
            this.episodes = episodes;
        }

        @Override
        public double getAverageRating() {
            List<Double> top3 = episodes.stream()
                    .map(l -> l.stream().mapToDouble(i -> i).average().orElse(.0) *
                            Math.min(l.size() / 20.0, 1.0))
                    .sorted(Comparator.reverseOrder())
                    .limit(3)
                    .collect(Collectors.toCollection(ArrayList::new));
            return top3.stream().mapToDouble(i -> i).average().orElse(.0);
        }

        @Override
        public String toString() {
            return String.format("TV Show %s %.4f (%d episodes)", getTitle(), getAverageRating(), episodes.size());
        }
    }

    static class WatchableFactory {

        // Spider-Man: No Way Home;Action,Adventure,Sci-Fi;8 9 7 9 10 8 10 9 10 8 8 9 9 10 10 8 9 10 8 9 10 8 8 9 10
        static Movie movie(String[] tokens) {
            String title = tokens[0];
            List<String> genres = List.of(tokens[1].split(","));

            List<Integer> ratings = new ArrayList<>();
            String[] r = tokens[2].split(" ");
            Arrays.stream(r).forEach(i -> ratings.add(Integer.parseInt(i)));

            return new Movie(title, genres, ratings);
        }

        // Friends;Comedy,Romance;S1E1 9 9 8 8 10 9 8 9 10 8 10 8 9 10 9 8 9 8 10 9 10 8 9 10 8;
        // S1E2 8 9 8 10 9 8 9 10 8 9 7 7 7 7 8 8 9 9 9 9;S1E3 9 9 8 8 9 8 10 10 8 9 8 9 8 9 8 8 9 10 8 8;
        // S1E4 8 10 8 9 9 8 9 10 8 9 10 10 10 8 8 10 9 8 8 8;S1E5 8 10 8 9 9 8 9 10 8 9 7 7 7 7 8 8 9 9 9 9;
        // S1E6 9 9 8 8 9 8 10 10 8 9 8 9 8 9 8 8 9 10 8 8
        static Series series(String[] tokens) {
            String title = tokens[0];
            List<String> genres = List.of(tokens[1].split(","));

            List<List<Integer>> episodes = new ArrayList<>();
            for (int i = 2; i < tokens.length; i++) {
                String[] r = tokens[i].split(" ");
                List<Integer> ratings = new ArrayList<>();
                Arrays.stream(r).skip(1).forEach(j -> ratings.add(Integer.parseInt(j)));
                episodes.add(ratings);
            }

            return new Series(title, genres, episodes);
        }

        static Watchable create(String s) {
            String[] tokens = s.split(";");
            if (tokens.length <= 3)
                return movie(tokens);
            else
                return series(tokens);
        }
    }

    static class StreamingPlatform {
        private final Collection<Watchable> collection;

        StreamingPlatform() {
            collection = new ArrayList<>();
        }

        void addItem(String data) {
            collection.add(WatchableFactory.create(data));
        }

        void listAllItems() {
            PrintWriter pw = new PrintWriter(System.out);
            collection.stream()
                    .sorted()
                    .forEach(pw::println);
            pw.flush();
        }

        void listFromGenre (String genre) {
            PrintWriter pw = new PrintWriter(System.out);
            collection.stream()
                    .filter(w -> w.getGenres().contains(genre))
                    .sorted()
                    .forEach(pw::println);
            pw.flush();
        }
    }
}
