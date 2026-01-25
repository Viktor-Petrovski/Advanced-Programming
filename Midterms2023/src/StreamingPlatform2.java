import java.util.*;
import java.util.stream.Collectors;

public class StreamingPlatform2 {

    static class CosineSimilarityCalculator {

        public static double cosineSimilarity(Map<String, Integer> c1, Map<String, Integer> c2) {
            return cosineSimilarity(c1.values(), c2.values());
        }

        public static double cosineSimilarity(Collection<Integer> c1, Collection<Integer> c2) {
            int[] array1;
            int[] array2;
            array1 = c1.stream().mapToInt(i -> i).toArray();
            array2 = c2.stream().mapToInt(i -> i).toArray();
            double up = 0.0;
            double down1 = 0, down2 = 0;

            for (int i = 0; i < c1.size(); i++) {
                up += (array1[i] * array2[i]);
            }

            for (int i = 0; i < c1.size(); i++) {
                down1 += (array1[i] * array1[i]);
            }

            for (int i = 0; i < c1.size(); i++) {
                down2 += (array2[i] * array2[i]);
            }

            return up / (Math.sqrt(down1) * Math.sqrt(down2));
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StreamingPlatform sp = new StreamingPlatform();

        while (sc.hasNextLine()){
            String line = sc.nextLine();
            String [] parts = line.split("\\s+");

            if (parts[0].equals("addMovie")) {
                String id = parts[1];
                String name = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
                sp.addMovie(id ,name);
            } else if (parts[0].equals("addUser")){
                String id = parts[1];
                String name = parts[2];
                sp.addUser(id ,name);
            } else if (parts[0].equals("addRating")){
                //String userId, String movieId, int rating
                String userId = parts[1];
                String movieId = parts[2];
                int rating = Integer.parseInt(parts[3]);
                sp.addRating(userId, movieId, rating);
            } else if (parts[0].equals("topNMovies")){
                int n = Integer.parseInt(parts[1]);
                System.out.println("TOP " + n + " MOVIES:");
                sp.topNMovies(n);
            } else if (parts[0].equals("favouriteMoviesForUsers")) {
                List<String> users = Arrays.stream(parts).skip(1).collect(Collectors.toList());
                System.out.println("FAVOURITE MOVIES FOR USERS WITH IDS: " + String.join(", ", users));
                sp.favouriteMoviesForUsers(users);
            } else if (parts[0].equals("similarUsers")) {
                String userId = parts[1];
                System.out.println("SIMILAR USERS TO USER WITH ID: " + userId);
                sp.similarUsers(userId);
            }
        }
    }

    static class User {
        private final String id;
        private final String username;
        private final Map<String, Integer> ratings; // movieId -> rating

        User(String id, String username) {
            this.id = id;
            this.username = username;
            ratings = new HashMap<>();
        }

        void populate(Collection<String> movieIds) {
            movieIds.forEach(i -> ratings.putIfAbsent(i, 0));
        }

        public Map<String, Integer> getRatings() {
            return ratings;
        }

        void addRating(String movieId, int rating) {
            ratings.put(movieId, rating);
        }

        Collection<String> favoriteMovies() {
            int maxRating = ratings.values().stream().mapToInt(i -> i).max().orElse(0);
            return ratings.entrySet().stream().filter(e -> e.getValue() == maxRating)
                    .map(Map.Entry::getKey).collect(Collectors.toCollection(HashSet::new));
        }

        //User ID: 1 Name: User1
        @Override
        public String toString() {
            return String.format("User ID: %s Name: %s", id, username);
        }

    }


    static class Movie {
        private final String id;
        private final String title;
        private final List<Integer> ratings;

        Movie(String id, String title) {
            this.id = id;
            this.title = title;
            ratings = new ArrayList<>();
        }

        void addRating(int rating) {
            ratings.add(rating);
        }

        double averageRating() {
            return ratings.stream().mapToInt(i -> i).average().orElse(.0);
        }

        @Override
        public String toString() {
            return String.format("Movie ID: %s Title: %s Rating: %.2f", id, title, averageRating());
        }
    }

    static class StreamingPlatform {
        private final Map<String, Movie> movieMap;
        private final Map<String, User> userMap;

        StreamingPlatform() {
            movieMap = new HashMap<>();
            userMap = new TreeMap<>();
        }

        void addMovie (String id, String name) {
            movieMap.put(id, new Movie(id, name));
        }

        void addUser (String id, String username) {
            userMap.put(id, new User(id, username));
        }

        void addRating (String userId, String movieId, int rating) {
            User user = userMap.get(userId);
            Movie movie = movieMap.get(movieId);

            movie.addRating(rating);
            user.addRating(movieId, rating);
        }

        void topNMovies (int n) {
            movieMap.values().stream().sorted(Comparator.comparing(Movie::averageRating).reversed()).limit(n)
                    .forEach(System.out::println);
        }

        void favouriteMoviesForUsers(List<String> userIds) {
            userIds.forEach(i -> {
                User u = userMap.get(i);
                System.out.println(u);
                u.favoriteMovies().stream()
                        .map(movieMap::get)
                        .sorted(Comparator.comparing(Movie::averageRating).reversed())
                        .forEach(System.out::println);
                System.out.println();
            });
        }

        public static String fmt(double d) {
            return (d == (long) d) ? String.format("%d",(long)d) : String.format("%s",d);
        }


        void similarUsers(String userId) {
            userMap.values().forEach(u -> u.populate(new ArrayList<>(movieMap.keySet())));
            // во мапата со рејтинзи додади нули за филмовите кои не ги оценил корисникот

            User centroid = userMap.get(userId);
            Comparator<User> comparator = Comparator.comparing(u ->
                    CosineSimilarityCalculator.cosineSimilarity(u.getRatings(), centroid.getRatings()));

            userMap.entrySet().stream()
                    .filter(e -> !e.getKey().equalsIgnoreCase(userId)) // not self
                    .map(Map.Entry::getValue)
                    .sorted(comparator.reversed())

                    .forEach(u -> System.out.printf("%s %s\n", u, fmt(CosineSimilarityCalculator.cosineSimilarity(u.getRatings(), centroid.getRatings()))));
        }
    }
}
