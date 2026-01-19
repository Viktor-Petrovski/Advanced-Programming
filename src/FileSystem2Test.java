import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FileSystem2Test {
    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            fileSystem.addFile(parts[0].charAt(0), parts[1],
                    Integer.parseInt(parts[2]),
                    LocalDateTime.of(2016, 12, 29, 0, 0, 0).minusDays(Integer.parseInt(parts[3]))
            );
        }
        int action = scanner.nextInt();
        if (action == 0) {
            scanner.nextLine();
            int size = scanner.nextInt();
            System.out.println("== Find all hidden files with size less then " + size);
            List<File> files = fileSystem.findAllHiddenFilesWithSizeLessThan(size);
            files.forEach(System.out::println);
        } else if (action == 1) {
            scanner.nextLine();
            String[] parts = scanner.nextLine().split(":");
            System.out.println("== Total size of files from folders: " + Arrays.toString(parts));
            int totalSize = fileSystem.totalSizeOfFilesFromFolders(Arrays.stream(parts)
                    .map(s -> s.charAt(0))
                    .collect(Collectors.toList()));
            System.out.println(totalSize);
        } else if (action == 2) {
            System.out.println("== Files by year");
            Map<Integer, Set<File>> byYear = fileSystem.byYear();
            byYear.keySet().stream().sorted()
                    .forEach(key -> {
                        System.out.printf("Year: %d\n", key);
                        Set<File> files = byYear.get(key);
                        files.stream()
                                .sorted()
                                .forEach(System.out::println);
                    });
        } else if (action == 3) {
            System.out.println("== Size by month and day");
            Map<String, Long> byMonthAndDay = fileSystem.sizeByMonthAndDay();
            byMonthAndDay.keySet().stream().sorted()
                    .forEach(key -> System.out.printf("%s -> %d\n", key, byMonthAndDay.get(key)));
        }
        scanner.close();
    }

    static class File implements Comparable<File> {
        final String name;
        final int size;
        final LocalDateTime createdAt;

        File(String name, int size, LocalDateTime createdAt) {
            this.name = name;
            this.size = size;
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            // %-10[name] %5[size]B %[createdAt]
            return String.format("%-10s %5dB %s", name, size, createdAt);
        }

        @Override
        public int compareTo(File o) {
            Comparator<File> BY_DATE = Comparator.comparing(f -> f.createdAt);
            Comparator<File> BY_NAME = Comparator.comparing(f -> f.name);
            Comparator<File> BY_SIZE = Comparator.comparing(f -> f.size);

            Comparator<File> ALL = BY_DATE.thenComparing(BY_NAME).thenComparing(BY_SIZE);
            return ALL.compare(this, o);
        }
    }

    static class FileSystem {
        private final Map<Character, Collection<File>> map;

        FileSystem() {
            map = new TreeMap<>();
        }

        public void addFile(char folder, String name, int size, LocalDateTime createdAt) {
            Collection<File> files = map.computeIfAbsent(folder, k -> new TreeSet<>());
            files.add(new File(name, size, createdAt));
        }

        public List<File> findAllHiddenFilesWithSizeLessThan(int size) {
            return map.values().stream().flatMap(Collection::stream)
                    .filter(f -> f.name.startsWith(".") && f.size < size)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public int totalSizeOfFilesFromFolders(List<Character> folders) {
            return folders.stream()
                    .map(map::get)
                    .flatMap(Collection::stream)
                    .mapToInt(f -> f.size)
                    .sum();
        }

        public Map<Integer, Set<File>> byYear() {
            return map.values().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.groupingBy(f -> f.createdAt.getYear(), Collectors.toSet()));
        }

        public Map<String, Long> sizeByMonthAndDay() {
            return map.values().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.groupingBy(
                            this::getMonthDay,
                            Collectors.summingLong(f -> f.size)
                            )
                    );
        }

        private String getMonthDay(File f) {
            return f.createdAt.getMonth().toString() + "-" + f.createdAt.getDayOfMonth();
        }

    }
}
