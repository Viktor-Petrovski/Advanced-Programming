import java.util.*;
import java.util.stream.Collectors;

public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoefficient();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }

    static class Movie {
        private final String title;
        private final int[] ratings;

        Movie(String title, int[] ratings) {
            this.title = title;
            this.ratings = ratings;
        }

        double avgRating() {
            return Arrays.stream(ratings).average().orElse(.0);
        }

        public String getTitle() {
            return title;
        }

        public int getRatingsAmt() {
            return ratings.length;
        }

        @Override
        public String toString() {
            return String.format("%s (%.2f) of %d ratings", title, avgRating(), getRatingsAmt());
        }

        public static final Comparator<Movie> BY_AVG_RATING = Comparator.comparing(Movie::avgRating).reversed();
        public static final Comparator<Movie> BY_TITLE = Comparator.comparing(Movie::getTitle);
    }

    static class MoviesList {
        private final static int LIMIT = 10;
        private final Collection<Movie> movieCollection;

        MoviesList() {
            movieCollection = new ArrayList<>();
        }

        public void addMovie(String title, int[] ratings) {
            movieCollection.add(new Movie(title, ratings));
        }

        public List<Movie> top10ByAvgRating() {
            return movieCollection.stream()
                    .sorted(Movie.BY_AVG_RATING.thenComparing(Movie.BY_TITLE))
                    .limit(LIMIT)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private int maxRatings() {
            return movieCollection.stream().mapToInt(Movie::getRatingsAmt).max().orElse(0);
        }

        private double coefficient(Movie m) {
            return (m.avgRating() * m.getRatingsAmt()) / maxRatings();
        }

        public List<Movie> top10ByRatingCoefficient() {
            Comparator<Movie> BY_COEFFICIENT = (x, y) -> Double.compare(coefficient(y), coefficient(x));
            return movieCollection.stream()
                    .sorted(BY_COEFFICIENT.thenComparing(Movie.BY_TITLE))
                    .limit(LIMIT)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

    }
}
