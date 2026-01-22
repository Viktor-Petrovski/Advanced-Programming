import java.util.*;
import java.util.stream.IntStream;

public class FacultyTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();

        if (testCase == 1) {
            System.out.println("TESTING addStudent AND printFirstNStudents");
            Faculty faculty = new Faculty();
            for (int i = 0; i < 10; i++) {
                faculty.addStudent("student" + i, (i % 2 == 0) ? 3 : 4);
            }
            faculty.printFirstNStudents(10);

        } else if (testCase == 2) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            try {
                faculty.addGradeToStudent("123", 7, "NP", 10);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
            try {
                faculty.addGradeToStudent("1234", 9, "NP", 8);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        } else if (testCase == 3) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("123", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("1234", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (testCase == 4) {
            System.out.println("Testing addGrade for graduation");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            int counter = 1;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("123", i, "course" + counter, (i % 2 == 0) ? 7 : 8);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            counter = 1;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("1234", i, "course" + counter, (j % 2 == 0) ? 7 : 10);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("PRINT STUDENTS (there shouldn't be anything after this line!");
            faculty.printFirstNStudents(2);
        } else if (testCase == 5 || testCase == 6 || testCase == 7) {
            System.out.println("Testing addGrade and printFirstNStudents (not graduated student)");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), i % 5 + 6);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            if (testCase == 5)
                faculty.printFirstNStudents(10);
            else if (testCase == 6)
                faculty.printFirstNStudents(3);
            else
                faculty.printFirstNStudents(20);
        } else if (testCase == 8 || testCase == 9) {
            System.out.println("TESTING DETAILED REPORT");
            Faculty faculty = new Faculty();
            faculty.addStudent("student1", ((testCase == 8) ? 3 : 4));
            int grade = 6;
            int counterCounter = 1;
            for (int i = 1; i < ((testCase == 8) ? 6 : 8); i++) {
                for (int j = 1; j < 3; j++) {
                    try {
                        faculty.addGradeToStudent("student1", i, "course" + counterCounter, grade);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    grade++;
                    if (grade == 10)
                        grade = 5;
                    ++counterCounter;
                }
            }
            System.out.println(faculty.getDetailedReportForStudent("student1"));
        } else if (testCase==10) {
            System.out.println("TESTING PRINT COURSES");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            faculty.printCourses();
        } else if (testCase==11) {
            System.out.println("INTEGRATION TEST");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 2 : 3); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }

            }

            for (int i=11;i<15;i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= 3; k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("DETAILED REPORT FOR STUDENT");
            System.out.println(faculty.getDetailedReportForStudent("student2"));
            try {
                System.out.println(faculty.getDetailedReportForStudent("student11"));
                System.out.println("The graduated students should be deleted!!!");
            } catch (NullPointerException e) {
                System.out.println("The graduated students are really deleted");
            }
            System.out.println("FIRST N STUDENTS");
            faculty.printFirstNStudents(10);
            System.out.println("COURSES");
            faculty.printCourses();
        }
    }

    static class OperationNotAllowedException extends Exception {
        public OperationNotAllowedException(String message) {
            super(message);
        }
    }

    static class Student {
        private final String id;
        private final int yearsOfStudies;
        private final Set<String> courses;

        private final Map<Integer, List<Integer>> semesterMap;

        Student(String id, int yearsOfStudies) {
            this.id = id;
            this.yearsOfStudies = yearsOfStudies;
            semesterMap = new TreeMap<>();
            courses = new TreeSet<>();

            populateMap();
        }

        private void populateMap() {
            IntStream.range(1, (yearsOfStudies * 2) + 1).forEach(i -> semesterMap.put(i, new ArrayList<>()));
        }

        public String getId() {
            return id;
        }

        boolean isValidTerm(int term) {
            return yearsOfStudies * 2 >= term;
        }

        boolean canAddGradeInTerm(int term) {
            int MAX_COURSES_PER_SEMESTER = 3;
            List<Integer> termGrades = semesterMap.get(term);
            return termGrades == null || termGrades.size() < MAX_COURSES_PER_SEMESTER;
        }

        void addGradeToTerm(int term, int grade, String course) {
            semesterMap.get(term).add(grade);
            courses.add(course);
        }

        private int amountCoursesPassed() {
            return (short) semesterMap.values().stream().mapToLong(Collection::size).sum();
        }

        boolean isGraduated() {
            int CLASSES_PER_YEAR = 6;
            return (long) yearsOfStudies * CLASSES_PER_YEAR <= amountCoursesPassed();
        }

        double averageGrade() {
            double res = semesterMap.values().stream().flatMap(Collection::stream).mapToInt(i -> i).average().orElse(.0);
            return res >= 5 ? res : 5.0;
        }

        public String graduationDescription() {
            return String.format("Student with ID %s graduated with average grade %.2f in %d years.",
                    id, averageGrade(), yearsOfStudies);
        }

        public String shortDescription() {
            //Student: [id] Courses passed: [coursesPassed] Average grade: [averageGrade]
            return String.format("Student: %s Courses passed: %d Average grade: %.2f",
                    id, amountCoursesPassed(), averageGrade());

        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Student: %s\n", id));

            semesterMap.forEach((k, v) -> {
                double avg = v.stream().mapToDouble(i -> i).average().orElse(.0);
                avg = avg >= 5 ? avg : 5;
                sb.append(String.format("Term %d\n", k));
                sb.append(String.format("Courses: %d\n", v.size()));
                sb.append(String.format("Average grade for term: %.2f\n", avg));
            });

            sb.append(String.format("Average grade: %.2f\n", averageGrade()));
            sb.append(String.format("Courses attended: %s", String.join(",", courses)));
            return sb.toString();
        }


        public static final Comparator<Student> BY_AMOUNT_PASSED = Comparator.comparing(Student::amountCoursesPassed).reversed();
        public static final Comparator<Student> BY_AVERAGE = Comparator.comparing(Student::averageGrade).reversed();
        public static final Comparator<Student> BY_ID = Comparator.comparing(Student::getId).reversed();

        public static final Comparator<Student> COMBINED = BY_AMOUNT_PASSED.thenComparing(BY_AVERAGE).thenComparing(BY_ID);
    }

    static class Course {
        private final String name;
        private final List<Integer> grades;

        Course(String name) {
            this.name = name;
            grades = new ArrayList<>();
        }

        String getName() {
            return name;
        }

        private int students() {
            return grades.size();
        }

        private double avg() {
            double res = grades.stream().mapToDouble(i -> i).average().orElse(.0);
            return res >= 5 ? res : 5.0;
        }

        void addGrade(int grade) {
            grades.add(grade);
        }

        @Override
        public String toString() {
            return String.format("%s %d %.2f", name, students(), avg());
        }

        public static final Comparator<Course> BY_AMOUNT_STUDENTS_THEN_AVG =
                Comparator.comparing(Course::students).thenComparing(Course::avg).thenComparing(Course::getName);
    }

    static class Faculty {
        private final Map<String, Student> studentMap; // id -> student
        private final TreeSet<Student> studentSet;
        private final Set<Course> courses;
        private final List<String> logger;

        public Faculty() {
            studentMap = new HashMap<>();
            studentSet = new TreeSet<>(Student.COMBINED);
            courses = new TreeSet<>(Course.BY_AMOUNT_STUDENTS_THEN_AVG);
            logger = new ArrayList<>();
        }

        void addStudent(String id, int yearsOfStudies) {
            Student ins = new Student(id, yearsOfStudies);
            studentMap.put(id, ins);
            studentSet.add(ins);
        }

        void graduateStudent(String id) {
            logger.add(studentMap.get(id).graduationDescription());
            studentSet.remove(studentMap.get(id));
            studentMap.remove(id);
        }

        private Optional<Course> findCourse(String name) {
            return courses.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
        }


        void addGradeToStudent(String studentId, int term, String courseName, int grade) throws OperationNotAllowedException {
            Student student = studentMap.get(studentId);

            if (!student.isValidTerm(term))
                throw new OperationNotAllowedException(String.format("Term %d is not possible for student with ID %s", term, studentId));
            if (!student.canAddGradeInTerm(term))
                throw new OperationNotAllowedException(String.format("Student %s already has 3 grades in term %d", studentId, term));

            studentSet.remove(student);
            student.addGradeToTerm(term, grade, courseName);
            studentSet.add(student);

            if (student.isGraduated())
                graduateStudent(studentId);

            Optional<Course> course = findCourse(courseName);
            course.ifPresent(c -> {
                courses.remove(c);
                c.addGrade(grade);
                courses.add(c);
            });

            if (course.isEmpty()) {
                Course ins = new Course(courseName);
                ins.addGrade(grade);
                courses.add(ins);
            }
        }

        String getFacultyLogs() {
            return String.join("\n", logger);
        }

        String getDetailedReportForStudent(String id) {
            return studentMap.get(id).toString();
        }

        void printFirstNStudents(int n) {
            studentSet.stream().limit(n).forEach(s -> System.out.println(s.shortDescription()));
        }

        void printCourses() {
            courses.forEach(System.out::println);
        }
    }

}
