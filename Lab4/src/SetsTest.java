import java.util.*;
import java.util.stream.Collectors;


public class SetsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Faculty faculty = new Faculty();

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String[] tokens = input.split("\\s+");
            String command = tokens[0];

            switch (command) {
                case "addStudent":
                    String id = tokens[1];
                    List<Integer> grades = new ArrayList<>();
                    for (int i = 2; i < tokens.length; i++) {
                        grades.add(Integer.parseInt(tokens[i]));
                    }
                    try {
                        faculty.addStudent(id, grades);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case "addGrade":
                    String studentId = tokens[1];
                    int grade = Integer.parseInt(tokens[2]);
                    faculty.addGrade(studentId, grade);
                    break;

                case "getStudentsSortedByAverageGrade":
                    System.out.println("Sorting students by average grade");
                    Set<Student> sortedByAverage = faculty.getStudentsSortedByAverageGrade();
                    for (Student student : sortedByAverage) {
                        System.out.println(student);
                    }
                    break;

                case "getStudentsSortedByCoursesPassed":
                    System.out.println("Sorting students by courses passed");
                    Set<Student> sortedByCourses = faculty.getStudentsSortedByCoursesPassed();
                    for (Student student : sortedByCourses) {
                        System.out.println(student);
                    }
                    break;

                default:
                    break;
            }
        }

        scanner.close();
    }

    static class Student {
        private final String id;
        private final List<Integer> grades;

        Student(String id, List<Integer> grades) {
            this.id = id;
            this.grades = grades;
        }

        void addGrade(int grade) {
            grades.add(grade);
        }

        @Override
        public String toString() {
            return "Student{" +
                    "id='" + id + '\'' +
                    ", grades=" + grades +
                    '}';
        }

        public String getId() {
            return id;
        }

        private int passed() {
            return grades.size();
        }

        private double getAvg() {
            return grades.stream().mapToDouble(i -> i).average().orElse(.0);
        }

        public static final Comparator<Student> BY_ID = Comparator.comparing(Student::getId);
        public static final Comparator<Student> BY_PASSED = Comparator.comparing(Student::passed);
        public static final Comparator<Student> BY_AVG = Comparator.comparing(Student::getAvg);
        
        public static final Comparator<Student> FIRST = BY_PASSED.thenComparing(BY_AVG)
                .thenComparing(BY_ID).reversed();
    }

    static class Faculty {
        private final Map<String, Student> studentMap;

        Faculty() {
            studentMap = new HashMap<>();
        }

        void addStudent(String id, List<Integer> grades) throws Exception {
            if (studentMap.containsKey(id))
                throw new Exception(String.format("Student with ID %s already exists", id));

            studentMap.put(id, new Student(id, grades));
        }

        void addGrade(String id, int grade) {
            studentMap.get(id).addGrade(grade);
        }

        /// враќа Set<Student> од студенти сортирани во опаѓачки редослед според просечната оценка.
        /// Ако двајца студенти имаат иста просечна оценка,
        /// тие се сортираат според бројот на положени предмети, а потоа според ID-то во опаѓачки редослед.
        Set<Student> getStudentsSortedByAverageGrade() {
            Comparator<Student> comparator = Student.BY_AVG.reversed().thenComparing(Student.BY_PASSED.reversed())
                    .thenComparing(Student.BY_ID);

            return studentMap.values().stream()
                    .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
        }

        /// враќа Set<Student> од студенти сортирани во опаѓачки редослед според бројот на положени предмети.
        ///  Ако двајца студенти имаат ист број положени предмети, тие се сортираат според просечната оценка,
        ///  а потоа според ID-то, двата во опаѓачки редослед.
        Set<Student> getStudentsSortedByCoursesPassed() {
            Comparator<Student> comparator = Student.BY_PASSED.thenComparing(Student.BY_AVG)
                    .thenComparing(Student.BY_ID).reversed();

            return studentMap.values().stream()
                    .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
        }

    }
}
