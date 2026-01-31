
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class CourseTest {

    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                advancedProgrammingCourse.updateStudent(idNumber, activity, points);
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }

    static class InvalidPointsException extends Exception {
        // ignore
    }

    static class Student {
        private final String id;
        private final String name;
        private int midterm1;
        private int midterm2;
        private int labs;

        Student(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setPoints(String activity, int points) throws InvalidPointsException {
            if (points > 100 || points < 0 || activity.equalsIgnoreCase("labs") && points > 10)
                throw new InvalidPointsException();

            if (activity.equalsIgnoreCase("midterm1")) midterm1 = points;
            if (activity.equalsIgnoreCase("midterm2")) midterm2 = points;
            if (activity.equalsIgnoreCase("labs")) labs = points;
        }
        // midterm1,midterm2 и labs

        public double summaryPoints() {
            return midterm1 * 0.45 + midterm2 * 0.45 + labs;
        }

        public int getGrade() {
            return Math.max(5, (int) (summaryPoints() / 10) + 1);
        }

        public static final Comparator<Student> BY_SUMMARY_POINTS_DESC = Comparator.comparing(Student::summaryPoints).reversed();

        @Override
        public String toString() {
            return String.format("ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d",
                    id, name, midterm1, midterm2, labs, summaryPoints(), getGrade());
        }
    }

    static class AdvancedProgrammingCourse {
        private final Map<String, Student> studentMap;

        AdvancedProgrammingCourse() {
            studentMap = new HashMap<>();
        }

        void addStudent(Student ins) {
            studentMap.put(ins.getId(), ins);
        }

        /// метод за ажурирање на поените на студентот со индекс idNumber во активноста activity со поените points.
        /// Методот да има комплекност О(1)!
        /// Можни вредности за activity се midterm1,midterm2 и labs.
        /// Со помош на исклучоци да се игнорираат додавања на поени кои се невалидни (не е потребно да се печати порака).
        public void updateStudent(String idNumber, String activity, int points) {
            Student s = studentMap.get(idNumber);
            try {
                s.setPoints(activity, points);
            } catch (InvalidPointsException ignored) {
                // ignored exception, as specified
            }
        }

        /// ги враќа првите N најдобри положени студенти на предметот сортирани во опаѓачки редослед според вкупниот број на сумарни поени.
        public List<Student> getFirstNStudents(int n) {
            return studentMap.values().stream().sorted(Student.BY_SUMMARY_POINTS_DESC).limit(n)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        /// враќа мапа од оценките (5,6,7,8,9,10) со бројот на студенти кои ја добиле соодветната оценка.
        public Map<Integer, Integer> getGradeDistribution() {
            Map<Integer, Integer> map = studentMap.values().stream().collect(Collectors.groupingBy(
                    Student::getGrade,
                    TreeMap::new,
                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));

            IntStream.range(5, 11).forEach(i -> map.putIfAbsent(i, 0));
            return map;
        }

        /// печати основни статистики за вкупните поени (min,max,average,count) за сумарните поени на сите положени студенти.
        public void printStatistics() {
            List<Student> list = studentMap.values().stream().filter(s -> s.getGrade() > 5)
                    .collect(Collectors.toCollection(ArrayList::new));

            int count = list.size();
            double min = list.stream().mapToDouble(Student::summaryPoints).min().orElse(.0);
            double max = list.stream().mapToDouble(Student::summaryPoints).max().orElse(.0);
            double avg = list.stream().mapToDouble(Student::summaryPoints).average().orElse(.0);
            System.out.printf("Count: %s Min: %.2f Average: %.2f Max: %.2f", count, min, avg, max);
        }

    }
}
