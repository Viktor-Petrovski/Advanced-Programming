import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class MovieTheaterTester {
    public static void main(String[] args) {
        MovieTheater mt = new MovieTheater();
        mt.readMovies();
        System.out.println("SORTING BY RATING");
        mt.printByRatingAndTitle();
        System.out.println("\nSORTING BY GENRE");
        mt.printByGenreAndTitle();
        System.out.println("\nSORTING BY YEAR");
        mt.printByYearAndTitle();
    }

    static class Movie {
        private final String title;
        private final String genre;
        private final int year;
        private final double avgRating;

        Movie(String title, String genre, int year, double avgRating) {
            this.title = title;
            this.genre = genre;
            this.year = year;
            this.avgRating = avgRating;
        }

        public String getGenre() {
            return genre;
        }

        public double getAvgRating() {
            return avgRating;
        }

        @Override
        public String toString() {
            return String.format("%s, %s, %d, %.2f", title, genre, year, avgRating);
        }

        public static final Comparator<Movie> BY_TITLE = Comparator.comparing(m -> m.title);
        public static final Comparator<Movie> BY_GENRE = Comparator.comparing(m -> m.genre);
        public static final Comparator<Movie> BY_YEAR = Comparator.comparing(m -> m.year);
        public static final Comparator<Movie> BY_AVG_RATING = Comparator.comparing(m -> m.avgRating);
    }

    static class MovieTheater {
        private final Collection<Movie> movieCollection;

        MovieTheater() {
            movieCollection = new ArrayList<>();
        }

        void readMovies() {
            Scanner sc = new Scanner(System.in);
            int n = Integer.parseInt(sc.nextLine().trim());

            for (int i = 0; i < n; i++) {
                String title = sc.nextLine().trim();
                String genre = sc.nextLine().trim();
                int year = Integer.parseInt(sc.nextLine().trim());
                String ratings = sc.nextLine().trim();
                double avgRating = Arrays.stream(ratings.split(" ")).mapToInt(Integer::parseInt).average().orElse(.0);

                movieCollection.add(new Movie(title, genre, year, avgRating));

            }
        }

        void printByGenreAndTitle() {
            movieCollection.stream().sorted(Movie.BY_GENRE.thenComparing(Movie.BY_TITLE)).forEach(System.out::println);
        } //- ги прикажува филмовите сортирани според жанр, па според наслов

        void printByYearAndTitle() {
            movieCollection.stream().sorted(Movie.BY_YEAR.thenComparing(Movie.BY_TITLE)).forEach(System.out::println);

        } // ги прикажува филмовите сортирани според година, па според наслов

        void printByRatingAndTitle() {
            movieCollection.stream().sorted(Movie.BY_AVG_RATING.reversed().thenComparing(Movie.BY_TITLE)).forEach(System.out::println);
        } // - ги прикажува филмовите сортирани според оцена, па според наслов


        /// враќа мапа на сите филмови каде клуч е жанрот на кој припаѓаат
        Map<String, List<Movie>> groupByGenre() {
            return movieCollection.stream().collect(Collectors.groupingBy(
                    Movie::getGenre,
                    TreeMap::new,
                    Collectors.toCollection(ArrayList::new)
            ));
        }

        /// враќа мапа на сите жанрови и сума на рејтинзите од филмовите. кои припаѓаат на нив
        Map<String, Double> ratingByGenre() {
            return movieCollection.stream().collect(Collectors.groupingBy(
                    Movie::getGenre,
                    TreeMap::new,
                    Collectors.summingDouble(Movie::getAvgRating)
            ));

        }

    }
}