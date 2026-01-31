import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class MailingListTest {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(br.readLine());

        Map<String, MailingList> mailingLists = new HashMap<>();
        Map<String, User> usersByEmail = new HashMap<>();

        for (int i = 0; i < n; i++) {
            String line = br.readLine();
            String[] parts = line.split(" ");

            String command = parts[0];

            switch (command) {

                case "CREATE_LIST": {
                    String listName = parts[1];
                    mailingLists.put(listName, new SimpleMailingList(listName));
                    break;
                }

                case "ADD_USER": {
                    String listName = parts[1];
                    String type = parts[2];
                    String name = parts[3];
                    String email = parts[4];

                    User user;
                    if (type.equals("NORMAL")) {
                        user = new MailingListUser(name, email);
                    } else if (type.equals("FILTERED")) {
                        String keyword = parts[5];
                        user = new FilteredMailingListUser(name, email, keyword);
                    } else { // ADMIN
                        user = new AdminUser(name, email);
                    }

                    usersByEmail.put(email, user);
                    mailingLists.get(listName).subscribe(user);
                    break;
                }

                case "REMOVE_USER": {
                    String listName = parts[1];
                    String email = parts[2];

                    User user = usersByEmail.get(email);
                    mailingLists.get(listName).unsubscribe(user);
                    break;
                }

                case "PUBLISH": {
                    String listName = parts[1];
                    String text = line.substring(line.indexOf(listName) + listName.length() + 1);
                    mailingLists.get(listName).publish(text);
                    break;
                }
            }
        }
    }

    // observer
    abstract static class User {
        protected final String name;
        protected final String email;
        protected final Map<String, Collection<String>> mailingListMap; // listName -> collection<articles>

        User(String name, String email) {
            this.name = name;
            this.email = email;

            mailingListMap = new HashMap<>();
        }

        abstract void notify(String mailingListName, String text);
    }

    static class MailingListUser extends User {

        MailingListUser(String name, String email) {
            super(name, email);
        }

        @Override
        public void notify(String mailingListName, String text) {
            mailingListMap.computeIfAbsent(mailingListName, k -> new HashSet<>()).add(text);

            System.out.printf("[USER] %s received email from %s: %s\n", name, mailingListName, text);
        }

    }

    static class FilteredMailingListUser extends User {
        private final String keyword;

        FilteredMailingListUser(String name, String email, String keyword) {
            super(name, email);
            this.keyword = keyword;
        }

        @Override
        public void notify(String mailingListName, String text) {
            mailingListMap.computeIfAbsent(mailingListName, k -> new HashSet<>()).add(text);

            if (text.toLowerCase().contains(keyword.toLowerCase()))
                System.out.printf("[FILTERED USER] %s received filtered email from %s: %s\n", name, mailingListName, text);
        }
    }

    static class AdminUser extends User {

        AdminUser(String name, String email) {
            super(name, email);
        }

        @Override
        public void notify(String mailingListName, String text) {
            mailingListMap.computeIfAbsent(mailingListName, k -> new HashSet<>()).add(text);
            System.out.printf("[ADMIN LOG] MailingList=%s | Message=%s\n", mailingListName, text);
        }
    }

    // observable
    interface MailingList {
        void subscribe(User user);

        void unsubscribe(User user);

        void publish(String text);
    }

    static class SimpleMailingList implements MailingList {
        private final String listName;
        private final Collection<User> subscribedUsers;

        SimpleMailingList(String listName) {
            this.listName = listName;
            subscribedUsers = new ArrayList<>();
        }

        @Override
        public void subscribe(User user) {
            subscribedUsers.add(user);
        }

        @Override
        public void unsubscribe(User user) {
            subscribedUsers.remove(user);
        }

        @Override
        public void publish(String text) {
            subscribedUsers.forEach(u -> u.notify(listName, text));
        }
    }
}
