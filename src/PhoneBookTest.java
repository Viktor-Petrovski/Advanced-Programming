import java.util.*;
import java.util.stream.Collectors;

public class PhoneBookTest {

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }

    static class DuplicateNumberException extends Exception {
        public DuplicateNumberException(String number) {
            super(String.format("Duplicate number: %s", number));
        }
    }

    static class PhoneBook {
        private final Map<String, String> phoneMap; // number -> name
        private final Map<String, Set<String>> nameMap; // name -> numbers

        PhoneBook() {
            phoneMap = new TreeMap<>();
            nameMap = new HashMap<>();
        }

        void addContact(String name, String number) throws DuplicateNumberException {
            if (phoneMap.containsKey(number))
                throw new DuplicateNumberException(number);

            phoneMap.put(number, name);
            nameMap.computeIfAbsent(name, k -> new TreeSet<>()).add(number);
        }

        void contactsByNumber(String number) {
            List<Map.Entry<String, String>> res = phoneMap.entrySet().stream()
                    .filter(e -> e.getKey().contains(number))
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toCollection(ArrayList::new));

            if (res.isEmpty()) {
                System.out.println("NOT FOUND");
                return;
            }

            res.forEach(e -> System.out.printf("%s %s\n",
                    e.getValue(), e.getKey()));
        }

        void contactsByName(String name) {
            if (!nameMap.containsKey(name)) {
                System.out.println("NOT FOUND");
                return;
            }

            nameMap.get(name).forEach(num -> System.out.printf("%s %s\n", name, num));
        }
    }

}
