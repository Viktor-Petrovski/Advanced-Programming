import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class NewsSystemTest {

    static class Article {

        private final String category;
        private final String author;
        private final String content;
        private final LocalDateTime timestamp;

        public Article(String category, String author, String content, LocalDateTime timestamp) {
            this.category = category;
            this.author = author;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getCategory() {
            return category;
        }

        public String getAuthor() {
            return author;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s\n%s", timestamp, author, category, content);
        }
    }

    public static void main(String[] args) {

        NewsSystem system = new NewsSystem();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String command = parts[0];

            switch (command) {

                case "ADD_USER":
                    system.addUser(parts[1]);
                    break;

                case "SUBSCRIBE_CATEGORY": {
                    String[] p = parts[1].split("\\s+");
                    system.subscribeUserToCategory(p[0], p[1]);
                    break;
                }

                case "UNSUBSCRIBE_CATEGORY": {
                    String[] p = parts[1].split("\\s+");
                    system.unsubscribeUserFromCategory(p[0], p[1]);
                    break;
                }

                case "SUBSCRIBE_AUTHOR": {
                    String[] p = parts[1].split("\\s+");
                    system.subscribeUserToAuthor(p[0], p[1]);
                    break;
                }

                case "UNSUBSCRIBE_AUTHOR": {
                    String[] p = parts[1].split("\\s+");
                    system.unsubscribeUserFromAuthor(p[0], p[1]);
                    break;
                }

                case "PUBLISH": {
                    // format:
                    // PUBLISH <category> <author> <timestamp> <content>
                    String[] p = parts[1].split("\\s+", 4);
                    Article article = new Article(
                            p[0],
                            p[1],
                            p[3],
                            LocalDateTime.parse(p[2])
                    );
                    system.publishArticle(article);
                    break;
                }

                case "PRINT":
                    system.printNewsForUser(parts[1]);
                    break;
            }
        }
    }

    static class User {
        private final String username;
        private final List<Article> notifications;

        User(String username) {
            this.username = username;
            notifications = new ArrayList<>();
        }

        public void update(Article ins) {
            if (!notifications.contains(ins))
                notifications.add(ins);
        }

        @Override
        public String toString() {
            return String.format("News for user: %s\n%s", username,
                    notifications.stream().sorted(Comparator.comparing(Article::getTimestamp))
                            .map(Article::toString).distinct().collect(Collectors.joining("\n")));
        }
    }

    static class NewsSystem {
        private final Map<String, User> userMap;

        private final Map<String, Set<String>> subToCategory; // category -> set<username>
        private final Map<String, Set<String>> subToAuthors; // author -> set<username>

        NewsSystem() {
            userMap = new LinkedHashMap<>();
            subToCategory = new LinkedHashMap<>();
            subToAuthors = new LinkedHashMap<>();
        }

        public void addUser(String username) {
            userMap.put(username, new User(username));
        }

        public void subscribeUserToCategory(String username, String categoryName) {
            subToCategory.computeIfAbsent(categoryName, k -> new HashSet<>()).add(username);
        }

        public void unsubscribeUserFromCategory(String username, String categoryName) {
            subToCategory.get(categoryName).remove(username);
        }

        public void subscribeUserToAuthor(String username, String authorName) {
            subToAuthors.computeIfAbsent(authorName, k -> new HashSet<>()).add(username);
        }

        public void unsubscribeUserFromAuthor(String username, String authorName) {
            subToAuthors.get(authorName).remove(username);
        }

        public void publishArticle(Article article) {
            String category = article.getCategory();
            String author = article.getAuthor();

            if (subToCategory.containsKey(category))
                subToCategory.get(category).forEach(id -> userMap.get(id).update(article));
            if (subToAuthors.containsKey(author))
                subToAuthors.get(author).forEach(id -> userMap.get(id).update(article));

        }

        public void printNewsForUser(String username) {
            System.out.println(userMap.get(username));
        }

    }
}
