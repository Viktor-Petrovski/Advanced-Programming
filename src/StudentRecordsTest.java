import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords();
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable();
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution();
    }

    static class Record implements Comparable<Record> {
        private final String code;
        private final String direction;
        private final List<Integer> grades;

        Record(String s) {
            String[] tokens = s.split("\\s+");
            code = tokens[0];
            direction = tokens[1];
            grades = new ArrayList<>();

            Arrays.stream(tokens).skip(2).mapToInt(Integer::parseInt).forEach(grades::add);
        }

        public String getDirection() {
            return direction;
        }

        public List<Integer> getGrades() {
            return grades;
        }

        double getAvg() {
            return grades.stream().mapToDouble(i -> i).average().orElse(.0);
        }

        @Override
        public int compareTo(Record o) {
            int i = Double.compare(o.getAvg(), this.getAvg());
            return i != 0 ? i : this.code.compareTo(o.code);
        }

        @Override
        public String toString() {
            return String.format("%s %.2f", code, getAvg());
        }
    }

    static class StudentRecords {
        private final Map<String, Set<Record>> recordMap; // dir -> set<record>

        StudentRecords() {
            recordMap = new TreeMap<>();
        }

        int readRecords() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().map(Record::new).forEach(r -> {
                String dir = r.getDirection();
                recordMap.computeIfAbsent(dir, k -> new TreeSet<>()).add(r);
            });

            return (int) recordMap.values().stream().mapToLong(Collection::size).sum();
        }

        void writeTable() {
            recordMap.forEach((key, value) -> {
                System.out.println(key);
                value.forEach(System.out::println);
            });
        }

        private long amountTens(Set<Record> records) {
            return records.stream().flatMap(r -> r.getGrades().stream()).filter(i -> i == 10).count();
        }

        private Map<Integer, Long> distribution(Set<Record> records) {
            return records.stream().flatMap(r -> r.getGrades().stream())
                    .collect(Collectors.groupingBy(
                    i -> i,
                            TreeMap::new,
                            Collectors.counting()
            ));
        }

        private void show(Set<Record> records) {
            Map<Integer, Long> distribution = distribution(records);

            distribution.forEach((k, v) -> {
                int times = (int) (v / 10);
                times = v % 10 > 0 ? times + 1: times;
                String stars = "*".repeat(times);
                System.out.printf("%2d | %s(%d)\n", k, stars, v);
            });
        }

        void writeDistribution() {
            Comparator<String> BY_TENS = Comparator.comparing(k -> amountTens(recordMap.get(k)));
            List<String> keysOrdered = recordMap.keySet().stream().sorted(BY_TENS.reversed()).collect(Collectors.toCollection(ArrayList::new));

            keysOrdered.forEach(k -> {
                System.out.println(k);
                show(recordMap.get(k));
            });
        }

    }
}

