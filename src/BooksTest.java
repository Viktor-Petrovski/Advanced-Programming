import java.util.*;
import java.util.stream.Collectors;

public class BooksTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        BookCollection booksCollection = new BookCollection();
        Set<String> categories = fillCollection(scanner, booksCollection);
        System.out.println("=== PRINT BY CATEGORY ===");
        for (String category : categories) {
            System.out.println("CATEGORY: " + category);
            booksCollection.printByCategory(category);
        }
        System.out.println("=== TOP N BY PRICE ===");
        print(booksCollection.getCheapestN(n));
    }

    static void print(List<Book> books) {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    static TreeSet<String> fillCollection(Scanner scanner,
                                          BookCollection collection) {
        TreeSet<String> categories = new TreeSet<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            Book book = new Book(parts[0], parts[1], Float.parseFloat(parts[2]));
            collection.addBook(book);
            categories.add(parts[1]);
        }
        return categories;
    }

    static class Book {
        private final String title;
        private final String category;
        private final float price;

        Book(String title, String category, float price) {
            this.title = title;
            this.category = category;
            this.price = price;
        }

        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            // Book A (A) 29.41
            return String.format("%s (%s) %.2f", title, category, price);
        }

        public static final Comparator<Book> BY_TITLE = Comparator.comparing(b -> b.title);
        public static final Comparator<Book> BY_PRICE = Comparator.comparing(b -> b.price);
    }

    static class BookCollection {
        private final Collection<Book> books;

        BookCollection() {
            books = new ArrayList<>();
        }

        public void addBook(Book book) {
            books.add(book);
        }

        public void printByCategory(String category) {
            books.stream().filter(b -> b.getCategory().equalsIgnoreCase(category))
                    .sorted(Book.BY_TITLE.thenComparing(Book.BY_PRICE))
                    .forEach(System.out::println);
        }

        public List<Book> getCheapestN(int n) {
            return books.stream().sorted(Book.BY_PRICE.thenComparing(Book.BY_TITLE)).limit(n).collect(Collectors.toCollection(ArrayList::new));
        }
    }
}

