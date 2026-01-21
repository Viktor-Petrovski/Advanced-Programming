import java.util.*;
import java.util.stream.Collectors;

public class LabExercisesTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }

    static class Student {
        private final String index;
        private final List<Integer> points;

        Student(String index, List<Integer> points) {
            this.index = index;
            this.points = points;
        }

        public String getIndex() {
            return index;
        }

        boolean isFailed() {
            return points.size() < 8;
        }

        double summaryPoints() {
            return points.stream().mapToInt(i -> i).sum() / 10.0;
        }

        int getYear() {
            return 20 - Integer.parseInt(index.substring(0, 2));
        }

        @Override
        public String toString() {
            return String.format("%s %s %.2f", index, isFailed() ? "NO" : "YES", summaryPoints());
        }

        public static final Comparator<Student> BY_IDX = Comparator.comparing(Student::getIndex);
        public static final Comparator<Student> BY_PTS = Comparator.comparing(Student::summaryPoints);
    }

    static class LabExercises {
        private final Collection<Student> students;

        LabExercises() {
            students = new ArrayList<>();
        }

        public void addStudent (Student student) {
            students.add(student);
        }

        public void printByAveragePoints (boolean ascending, int n) {
            Comparator<Student> C = Student.BY_PTS.thenComparing(Student.BY_IDX);
            C = ascending ? C : C.reversed();

            students.stream().sorted(C).limit(n).forEach(System.out::println);
        }

        public List<Student> failedStudents () {
            return students.stream().filter(Student::isFailed)
                    .sorted(Student.BY_IDX.thenComparing(Student.BY_PTS))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public Map<Integer, Double> getStatisticsByYear() {
            return students.stream().filter(s -> !s.isFailed())
                    .collect(Collectors.groupingBy(
                            Student::getYear,
                            TreeMap::new,
                            Collectors.averagingDouble(Student::summaryPoints)
                    ));
        }
    }
}