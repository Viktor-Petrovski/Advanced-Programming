import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryTester {
    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String libraryName = br.readLine();
            if (libraryName == null) return;

            libraryName = libraryName.trim();
            LibrarySystem lib = new LibrarySystem(libraryName);

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("END")) break;
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");

                switch (parts[0]) {

                    case "registerMember": {
                        lib.registerMember(parts[1], parts[2]);
                        break;
                    }

                    case "addBook": {
                        String isbn = parts[1];
                        String title = parts[2];
                        String author = parts[3];
                        int year = Integer.parseInt(parts[4]);
                        lib.addBook(isbn, title, author, year);
                        break;
                    }

                    case "borrowBook": {
                        lib.borrowBook(parts[1], parts[2]);
                        break;
                    }

                    case "returnBook": {
                        lib.returnBook(parts[1], parts[2]);
                        break;
                    }

                    case "printMembers": {
                        lib.printMembers();
                        break;
                    }

                    case "printBooks": {
                        lib.printBooks();
                        break;
                    }

                    case "printBookCurrentBorrowers": {
                        lib.printBookCurrentBorrowers(parts[1]);
                        break;
                    }

                    case "printTopAuthors": {
                        lib.printTopAuthors();
                        break;
                    }

                    case "getBooksWaitingListSize": {
                        lib.getBooksWaitingListSize().forEach((k, v) -> System.out.printf("%s -> %d\n", k, v));
                        break;
                    }

                    case "getMembersGroupedByBorrowedCount": {
                        lib.getMembersGroupedByBorrowedCount().forEach((k, v) -> System.out.printf("%s -> %s\n", k, v));
                        break;
                    }

                    case "getMembersByBorrowActivity": {
                        lib.getMembersByBorrowActivity().forEach((k, v) -> System.out.printf("%s -> %s\n", k, v));
                        break;
                    }

                    case "getTopBorrowersSummary": {
                        lib.getTopBorrowersSummary().forEach((k, v) -> System.out.printf("%s -> %s\n", k, v));
                        break;
                    }

                    case "getAuthorDemandAnalysis": {
                        lib.getAuthorDemandAnalysis().forEach((k, v) -> System.out.printf("%s -> %.2f\n", k, v));
                        break;
                    }

                    case "getAuthorSpecialist": {
                        lib.getAuthorSpecialist(br.readLine()).ifPresent(System.out::println);
                        break;
                    }

                    case "getBorrowActivityHeatmap": {
                        lib.getBorrowActivityHeatmap().forEach((k, v) -> System.out.printf("%d books -> %d members\n", k, v));
                        break;
                    }

                    default:
                        break;
                }
            }

        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    static class Book implements Comparable<Book> {
        private final String isbn;
        private final String title;
        private final String author;
        private final int year;

        private int totalAvailable;
        private int totalBorrows;

        Book(String isbn, String title, String author, int year) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.year = year;

            totalAvailable = 0;
            totalBorrows = 0;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public int getYear() {
            return year;
        }

        void add() {
            totalAvailable++;
        }

        void borrow() {
            totalAvailable--;
            totalBorrows++;
        }

        private int getTotalBorrows() {
            return totalBorrows;
        }


        @Override
        public String toString() {
            return String.format("%s - \"%s\" by %s (%d), available: %d, total borrows: %d",
                    isbn, title, author, year, totalAvailable, totalBorrows);
        }

        public static final Comparator<Book> BY_TOTAL_BORROWED_DESC_THEN_YEAR =
                Comparator.comparing(Book::getTotalBorrows).reversed().thenComparing(Book::getYear);

        @Override
        public int compareTo(Book o) {
            return title.compareTo(o.title);
        }
    }

    static class Member {
        private final String id;
        private final String name;

        private int totalBorrows;
        private final Map<String, Book> borrowed;
        private final Set<Book> hasBorrowed; // минато

        Member(String id, String name) {
            this.id = id;
            this.name = name;

            totalBorrows = 0;
            borrowed = new HashMap<>();
            hasBorrowed = new HashSet<>();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Set<Book> getHasBorrowed() {
            return hasBorrowed;
        }

        boolean holdsBook(String isbn) {
            return borrowed.containsKey(isbn);
        }

        void borrowBook(Book b) {
            totalBorrows++;
            borrowed.put(b.getIsbn(), b);
        }

        Book returnBook(String isbn) {
            hasBorrowed.add(borrowed.get(isbn));
            return borrowed.remove(isbn);
        }

        private int getTotalBorrows() {
            return totalBorrows;
        }

        private int getBorrowedNow() {
            return borrowed.size();
        }

        @Override
        public String toString() {
            return String.format("%s (%s) - borrowed now: %d, total borrows: %d",
                    name, id, getBorrowedNow(), getTotalBorrows());
        }

        public static final Comparator<Member> BY_BORROWED_NOW_DESC_THEN_NAME =
                Comparator.comparing(Member::getBorrowedNow).reversed().thenComparing(Member::getName);
    }

    static class LibrarySystem {
        private final String name;
        private final Map<String, Member> memberMap; // id -> member

        private final Map<String, Book> bookMap; // isbn -> book
        private final Map<String, Integer> bookCopies; // isbn -> copies
        private final Map<String, Queue<String>> bookWaitingQueue; // isbn -> member ids waiting

        LibrarySystem(String name) {
            this.name = name;
            memberMap = new HashMap<>();

            bookMap = new HashMap<>();
            bookCopies = new HashMap<>();
            bookWaitingQueue = new HashMap<>();
        }

        /// регистрира член во останатите членови со тоа што секој член на почеток нема ниту една позајмена книга.
        void registerMember(String id, String fullName) {
            memberMap.put(id, new Member(id, fullName));
        }

        /// додава книга во библиотека, така што една книга може да има повеќе примероци.
        /// Ако веќе постои книга со ист ISBN, тогаш бројот на примероци се зголемува за 1.
        /// Ако не постои - се додава со 1 примерок.
        void addBook(String isbn, String title, String author, int year) {
            bookMap.putIfAbsent(isbn, new Book(isbn, title, author, year));
            bookWaitingQueue.put(isbn, new LinkedList<>());
            bookCopies.merge(isbn, 1, Integer::sum);

            bookMap.get(isbn).add();
        }

        /// Ако книгата не постои, се игнорира акцијата.
        /// Ако книгата постои, но нема слободни примероци, членот се става во листа на чекање за таа книга.
        /// Ако има слободен примерок: му се доделува на членот и бројот на слободни примероци се намалува.
        void borrowBook(String memberId, String isbn) {
            if (!bookMap.containsKey(isbn)) return;

            if (bookCopies.get(isbn) == 0) {
                bookWaitingQueue.get(isbn).add(memberId);
                return;
            }

            bookCopies.merge(isbn, 1, (old, nu) -> old - 1);
            memberMap.get(memberId).borrowBook(bookMap.get(isbn));

            bookMap.get(isbn).borrow();
        }

        /// кога член ќе врати книга:
        /// Бројот на слободни примероци се зголемува.
        /// Ако постои листа на чекање за таа книга
        /// на првиот член од листата автоматски му се доделува позајмица од книгата (исто како borrowBook).
        void returnBook(String memberId, String isbn) {
            Book returned = memberMap.get(memberId).returnBook(isbn);
            bookCopies.merge(returned.isbn, 1, Integer::sum);

            returned.add();

            Queue<String> q = bookWaitingQueue.get(isbn);
            if (!q.isEmpty())
                borrowBook(q.poll(), returned.getIsbn());
        }

        /// да се испечатат сите членови сортирани според број на позајмени книги (опаѓачки) па ако е исто,
        /// по името на членот (растечки).
        /// Пример за еден ред:
        ///
        /// Gor (id27) - borrowed now: 5, total borrows: 17
        void printMembers() {
            memberMap.values().stream().sorted(Member.BY_BORROWED_NOW_DESC_THEN_NAME)
                    .forEach(System.out::println);
        }

        /// да се испечатат сите книги сортирани според број на позајмувања досега (опаѓачки),
        /// па ако е исто по година на издавање (растечки).
        /// Пример за еден ред:
        ///
        /// isbn1 - “The Hobbit” by Goch (2025), available: 199, total borrows: 2
        void printBooks() {
            bookMap.values().stream().sorted(Book.BY_TOTAL_BORROWED_DESC_THEN_YEAR)
                    .forEach(System.out::println);
        }

        /// да се испечатат моменталните ID броеви на изнајмувачи на книгата со тој ISBN,
        /// сортирани и одделени со запирка.
        void printBookCurrentBorrowers(String isbn) {
            String res = memberMap.values().stream()
                    .filter(m -> m.holdsBook(isbn))
                    .map(Member::getId)
                    .sorted()
                    .collect(Collectors.joining(", "));

            System.out.println(res);
        }

        /// да се испечатат авторите сортирани според број на позајмувања на нивните книги (опаѓачки),
        /// па ако е исто по име (растечки).
        /// Пример за еден ред:
        ///
        /// Goc - 127
        void printTopAuthors() {
            Map<String, Integer> res = bookMap.values().stream().collect(Collectors.groupingBy(
                    Book::getAuthor,
                    Collectors.mapping(
                            Book::getTotalBorrows,
                            Collectors.summingInt(x -> x)
                    )
            ));

            Comparator<Map.Entry<String, Integer>> comparator = Map.Entry.comparingByValue();

            res.entrySet().stream().sorted(comparator.reversed().thenComparing(Map.Entry::getKey))
                    .forEach(e -> System.out.printf("%s - %d\n", e.getKey(), e.getValue()));
        }

        @Override
        public String toString() {
            return "LibrarySystem{" +
                    "name='" + name + '\'' +
                    ", memberMap=" + memberMap +
                    ", bookMap=" + bookMap +
                    '}';
        }


        // Additional requirements

        /// кој ќе врати мапа во која за секоја книга е наведено колку пати била позајмувана, сортирана според насловот на книгата
        Map<Book, Integer> getBooksAndNumberOfBorrowings() {
            return bookMap.values().stream()
                    .collect(Collectors.toMap(
                            b -> b,
                            Book::getTotalBorrows,
                            (oldValue, newValue) -> oldValue,
                            TreeMap::new)
                    );
        }

        /// кој ќе врати мапа каде клуч е името на авторот, а вредност е TreeSet
        /// што ги содржи ISBN броевите на сите книги кои ги има напишано тој автор.
        Map<String, TreeSet<String>> getAuthorsWithBooks() {
            return bookMap.values().stream().collect(Collectors.groupingBy(
                    Book::getAuthor,
                    Collectors.mapping(
                            Book::getIsbn,
                            Collectors.toCollection(TreeSet::new)
                    )
            ));
        }

        /// кој ќе врати мапа каде што клуч е името на авторот, а вредност е книгата што има најмногу позајмувања од тој автор.
        Map<String, Optional<Book>> getTopBookPerAuthor() {
            return bookMap.values().stream().collect(Collectors.groupingBy(
                    Book::getAuthor,
                    Collectors.maxBy(Comparator.comparing(Book::getTotalBorrows))
            ));
        }

        /// кој ќе врати мапа каде клуч е ISBN бројот на книгата, а вредност е бројот на членови што чекаат на таа книга
        /// (големина на waiting list). Резултатната мапа треба да биде сортирана според големината на листата за чекање (опаѓачки),
        /// а ако е исто, според ISBN (растечки).
        Map<String, Integer> getBooksWaitingListSize() {
            Comparator<String> comparator = Comparator.comparing(s -> bookWaitingQueue.get(s).size());
            return bookMap.values().stream()
                    .collect(Collectors.toMap(
                            Book::getIsbn,
                            b -> bookWaitingQueue.get(b.getIsbn()).size(),
                            (old, nu) -> old,
                            () -> new TreeMap<>(comparator.reversed().thenComparing(s -> s))
                    ));
        }

        /// кој ќе ги групира сите членови во две групи, во зависност дали кога било имаат позајмено книга или не.
        Map<Boolean, List<Member>> getMembersByBorrowActivity() {
            return memberMap.values().stream().collect(Collectors.partitioningBy(m -> m.totalBorrows > 0));
        }

        /// кој ги групира членoвите според бројот на моментално позајмени книги.
        /// Мапата треба да биде сортирана според бројот на позајмени книги опаѓачки.
        Map<Integer, Set<Member>> getMembersGroupedByBorrowedCount() {
            return memberMap.values().stream()
                    .collect(Collectors.groupingBy(
                            Member::getBorrowedNow,
                            TreeMap::new,
                            Collectors.toSet()
                    ))
                    .descendingMap(); // !IMPORTANT
        }


        // AI генерирани проблеми за пракса :)
        // Courtesy of Gemini

        /**
         * Generates a report of the most active borrowers in the system.
         *  @return a Map where the key is the Member ID and the value is a list of
         * Book Titles they have borrowed.
         * @implNote
         * 1. Only include members who have borrowed at least 3 books total.<br>
         * 2. The map must be sorted by the total number of borrows (descending).<br>
         * 3. The list of book titles must be sorted alphabetically.
         */
        Map<String, List<String>> getTopBorrowersSummary() {
            Comparator<Map.Entry<String, ArrayList<String>>> comparator = Comparator.comparing(e -> e.getValue().size());
            return memberMap.values().stream().filter(m -> m.getHasBorrowed().size() > 3)
                    .collect(Collectors.toMap(
                            Member::getId,
                            m -> m.getHasBorrowed().stream().map(Book::getTitle).collect(Collectors.toCollection(ArrayList::new))
                    )).entrySet().stream().sorted(comparator.reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (x, y) -> x,
                            LinkedHashMap::new // МНОГУ БИТНО, инаку нема да се задрже редоследот
                    ));
        }

        /**
         * Analyzes the demand for books based on their authors.
         * @return a TreeMap where the key is the Author's Name and the value is the
         * average waiting list size for all books written by that author.
         * @implNote
         * 1. Exclude authors who have fewer than 2 books in the library. <br>
         * 2. The result must be sorted alphabetically by Author Name. <br>
         * 3. The average value should be represented as a Double.
         */
        Map<String, Double> getAuthorDemandAnalysis() {
            Map<String, TreeSet<String>> authorsWithBooks = getAuthorsWithBooks();

            return authorsWithBooks.entrySet().stream().filter(e -> e.getValue().size() > 1)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().stream().map(bookWaitingQueue::get)
                                    .mapToInt(Queue::size).average().orElse(.0),
                            (x, y) -> x,
                            TreeMap::new
                    ));
        }

        /**
         * Identifies the member who has the most experience with a specific author.
         * @param author The author to search for
         * @return an Optional containing the Member who has borrowed the highest
         * number of books in that author.
         * @implNote
         * 1. If two members have the same borrow count for the author,
         * choose the one with the smaller ID (older member).
         * 2. Returns Optional.empty() if no books in that author have been borrowed.
         */
        Optional<Member> getAuthorSpecialist(String author) {
            return memberMap.values().stream()
                    .max(Comparator.comparing((Member m) -> m.getHasBorrowed().stream()
                            .filter(b -> b.getAuthor().equals(author)).count())
                            .thenComparing(Member::getId));
        }

        /**
         * Creates a frequency distribution of current book borrows among members.
         * @return a Map where the key is the number of currently borrowed books
         * and the value is the total count of members in that bracket.
         * @implNote
         * 1. The map must be sorted by the number of books (the key) in ascending order.
         * 2. Use Collectors.counting() for the frequency calculation.
         */
        Map<Integer, Long> getBorrowActivityHeatmap() {
            return memberMap.values().stream().collect(Collectors.groupingBy(
                    Member::getBorrowedNow,
                    TreeMap::new,
                    Collectors.counting()
            ));
        }

    }
}

/*
GrandLibrary
registerMember M1 Leo
registerMember M2 Nina
registerMember M3 Pero
registerMember M4 Davor
registerMember M5 Jana
registerMember M6 Silvana
addBook A10 Hamlet Shakespeare 1603
addBook A10 Hamlet Shakespeare 1603
addBook A11 Macbeth Shakespeare 1606
addBook A20 Odyssey Homer 800
addBook A21 Iliad Homer 750
addBook A30 Utopia More 1516
addBook A40 Faust Goethe 1808
addBook A40 Faust Goethe 1808
borrowBook M1 A10
borrowBook M2 A10
borrowBook M3 A10
borrowBook M4 A10
returnBook M2 A10
borrowBook M5 A11
borrowBook M6 A10
returnBook M1 A10
borrowBook M2 A21
borrowBook M3 A20
borrowBook M4 A40
returnBook M3 A20
borrowBook M3 A40
borrowBook M1 A30
returnBook M4 A40
borrowBook M4 A40
borrowBook M5 A40
then enter the name of the method
 */