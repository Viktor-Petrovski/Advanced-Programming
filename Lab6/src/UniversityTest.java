//package midterms.m1;

import java.util.*;
import java.util.stream.*;


public class UniversityTest {

    static class Course {
        private final String code;
        private final String name;
        private final int credits;
        private final int difficulty;
        private final int enrolledStudents;

        public Course(String code, String name, int credits, int difficulty, int enrolledStudents) {
            this.code = code;
            this.name = name;
            this.credits = credits;
            this.difficulty = difficulty;
            this.enrolledStudents = enrolledStudents;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public int getCredits() {
            return credits;
        }

        public int getDifficulty() {
            return difficulty;
        }

        public int getEnrolledStudents() {
            return enrolledStudents;
        }

        @Override
        public String toString() {
            return String.format("%s (%s)", code, name);
        }
    }


    static class Department {
        private final String name;
        private final List<Course> courses;

        public Department(String name, List<Course> courses) {
            this.name = name;
            this.courses = courses;
        }

        public String getName() {
            return name;
        }

        public List<Course> getCourses() {
            return courses;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    static class University {

        private final List<Department> departments;

        public University(List<Department> departments) {
            this.departments = departments;
        }

        private Stream<Course> getCoursesStream() {
            return departments.stream()
                    .map(Department::getCourses)
                    .flatMap(Collection::stream);
        }

        public List<String> getAllCourseNames() {
            return getCoursesStream().map(Course::getName).collect(Collectors.toCollection(ArrayList::new));
        }

        public List<Course> getCoursesWithMinCredits(int minCredits) {
            return getCoursesStream()
                    .filter(c -> c.getCredits() >= minCredits)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public int getTotalStudentCount() {
            return getCoursesStream()
                    .mapToInt(Course::getEnrolledStudents)
                    .sum();
        }

        public Optional<Course> getHardestCourse() {
            return getCoursesStream()
                    .max(Comparator.comparing(Course::getDifficulty));
        }

        public Map<Integer, List<Course>> groupByDifficulty() {
            return getCoursesStream().collect(Collectors.groupingBy(
                    Course::getDifficulty,
                    TreeMap::new,
                    Collectors.toCollection(ArrayList::new)
            ));
        }

        public Map<String, Integer> getCourseEnrollmentMap() {
            return getCoursesStream().collect(Collectors.toMap(
                    Course::getCode,
                    Course::getEnrolledStudents
            ));
        }

        public double getAverageEnrollmentPerCourse() {
            return getCoursesStream().mapToInt(Course::getEnrolledStudents).average().orElse(.0);
        }

        public List<String> getSortedCourseCodes() {
            return getCoursesStream().map(Course::getCode).sorted(Comparator.naturalOrder())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public Map<String, List<String>> getDepartmentToCourseNames() {
            return departments.stream().collect(Collectors.toMap(
                    Department::getName,
                    d -> d.getCourses().stream().map(Course::getName)
                            .collect(Collectors.toCollection(ArrayList::new))
            ));
        }

        public List<Course> getAllCourses() {
            return getCoursesStream().collect(Collectors.toCollection(ArrayList::new));
        }

        public Optional<Department> getMostPopularDepartment() {
            return departments.stream().max(Comparator.comparing(d ->
                    d.getCourses().stream().mapToInt(Course::getEnrolledStudents).sum()));
        }

        public Map<Integer, Integer> getStudentsByDifficulty() {
            return getCoursesStream().collect(Collectors.groupingBy(
                    Course::getDifficulty,
                    Collectors.mapping(
                            Course::getEnrolledStudents,
                            Collectors.summingInt(i -> i)
                    )
            ));
        }

        public List<Course> getCoursesByDifficultyRange(int min, int max) {
            return getCoursesStream().filter(c -> c.getDifficulty() >= min && c.getDifficulty() <= max)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public List<String> getPopularCourseCodes(int minStudents) {
            return getCoursesStream().filter(c -> c.getEnrolledStudents() >= minStudents)
                    .map(Course::getCode)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public Map<String, Integer> getTotalCreditsPerDepartment() {
            return departments.stream().collect(Collectors.toMap(
                    Department::getName,
                    d -> d.getCourses().stream().mapToInt(Course::getCredits).sum()
            ));
        }

        public List<Course> getTop3HardestCourses() {
            return getCoursesStream()
                    .sorted(Comparator.comparing(Course::getDifficulty).reversed())
                    .limit(3).collect(Collectors.toCollection(ArrayList::new));
        }


        public Map<String, Double> getAverageDifficultyPerDepartment() {
            return departments.stream().collect(Collectors.toMap(
                    Department::getName,
                    d -> d.getCourses().stream().mapToInt(Course::getDifficulty).average().orElse(.0)
            ));
        }


        public IntSummaryStatistics getEnrollmentStatistics() {
            return getCoursesStream().mapToInt(Course::getEnrolledStudents).summaryStatistics();
        }

        /// Најди ги четирите департмани со најмалку студенти. <br>
        /// Спои ги со reduce. <br>
        /// Новото име треба да биде "DeptA & DeptB & DeptC & DeptD". <br>
        /// Спои ги нивните курсеви. <br>
        /// Врати нов University со ажурирана листа. <br>
        public University mergeFourSmallestDepartments() {
            List<Department> merged = departments.stream()
                    .sorted(Comparator.comparing(d -> d.getCourses()
                            .stream().mapToInt(Course::getEnrolledStudents).sum()))
                    .limit(4).collect(Collectors.toCollection(ArrayList::new));

            // останатите
            List<Department> regular = departments.stream()
                    .sorted(Comparator.comparing(d -> d.getCourses()
                            .stream().mapToInt(Course::getEnrolledStudents).sum()))
                    .skip(4).collect(Collectors.toCollection(ArrayList::new));

            List<Course> courses = merged.stream().flatMap(d -> d.getCourses().stream())
                    .distinct().collect(Collectors.toList());
            String name = merged.stream().map(Department::getName).collect(Collectors.joining(" & "));

            Department d = new Department(name, courses);

            List<Department> res = new ArrayList<>();
            res.add(d);
            res.addAll(regular);

            return new University(res);
        }

        /// кој враќа колку вкупно кредити носат сите курсеви
        int getTotalCredits() {
            return getCoursesStream().distinct().mapToInt(Course::getCredits).sum();
        }

        ///  кој враќа листа на департменти кои имаат барем еден тежок курс (курс кој надминува difficultyThreshold)
        List<Department> getDepartmentsWithAtLeastOneHardCourse(int difficultyThreshold) {
            return departments.stream()
                    .filter(d -> d.getCourses().stream().anyMatch(c -> c.getDifficulty() > difficultyThreshold))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        /// кој ги враќа сите курсеви кои имаат име што го содржи дадениот substring.
        List<Course> getCoursesWithNameContaining(String substring) {
            return getCoursesStream().distinct().filter(c -> c.getName().contains(substring))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        /// кој ќе го врати департментот кој има курсеви со најголем просечен difficulty.
        Optional<Department> getTopDepartmentByAverageDifficulty() {
            return departments.stream().max(
                    Comparator.comparingDouble(d -> d.getCourses().stream().mapToDouble(Course::getDifficulty).average().orElse(.0))
            );
        }

    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        List<Department> departments = new ArrayList<>();

        departments.add(new Department("Computer Science", Arrays.asList(
                new Course("SP", "Structural Programming", 6, 5, 1100),
                new Course("APS", "Algorithms", 6, 8, 500),
                new Course("OOP", "Object-Oriented Programming", 6, 7, 1000),
                new Course("NP", "Advanced Programming", 6, 9, 600)
        )));

        departments.add(new Department("Mathematics", Arrays.asList(
                new Course("CAL1", "Calculus I", 6, 6, 230),
                new Course("ALG", "Linear Algebra", 6, 7, 210),
                new Course("STAT", "Statistics", 6, 6, 170),
                new Course("CAL2", "Calculus II", 6, 8, 190)
        )));

        departments.add(new Department("Physics", Arrays.asList(
                new Course("PHY1", "Mechanics", 5, 7, 150),
                new Course("PHY2", "Electromagnetism", 5, 8, 140),
                new Course("PHY3", "Quantum Physics", 6, 9, 100),
                new Course("PHY4", "Optics", 4, 6, 130)
        )));

        departments.add(new Department("Chemistry", Arrays.asList(
                new Course("CH1", "Organic Chemistry", 6, 8, 120),
                new Course("CH2", "Inorganic Chemistry", 5, 7, 140),
                new Course("CH3", "Physical Chemistry", 6, 9, 110),
                new Course("CH4", "Analytical Chemistry", 5, 6, 130)
        )));

        departments.add(new Department("Biology", Arrays.asList(
                new Course("BIO1", "Cell Biology", 6, 7, 200),
                new Course("BIO2", "Genetics", 6, 8, 160),
                new Course("BIO3", "Ecology", 5, 6, 180),
                new Course("BIO4", "Biochemistry", 6, 9, 140)
        )));

        departments.add(new Department("Economics", Arrays.asList(
                new Course("MIC", "Microeconomics", 6, 6, 250),
                new Course("MAC", "Macroeconomics", 6, 7, 240),
                new Course("FIN", "Finance", 6, 8, 220),
                new Course("ACC", "Accounting", 6, 5, 260)
        )));

        departments.add(new Department("Languages", Arrays.asList(
                new Course("ENG", "English Language", 4, 4, 300),
                new Course("GER", "German Language", 4, 5, 180),
                new Course("FRE", "French Language", 4, 5, 160),
                new Course("ITA", "Italian Language", 4, 4, 170)
        )));

        departments.add(new Department("History", Arrays.asList(
                new Course("HIS1", "Ancient History", 5, 6, 140),
                new Course("HIS2", "Medieval History", 5, 7, 130),
                new Course("HIS3", "Modern History", 5, 6, 150),
                new Course("HIS4", "Contemporary History", 5, 7, 140)
        )));

        University university = new University(departments);

        String method = sc.nextLine().trim();

        System.out.println("Testing method: " + method);

        if (method.equals("getAllCourseNames")) {
            university.getAllCourseNames().forEach(System.out::println);

        } else if (method.equals("getCoursesWithMinCredits")) {
            int x = sc.nextInt();
            university.getCoursesWithMinCredits(x).forEach(System.out::println);

        } else if (method.equals("getTotalStudentCount")) {
            System.out.println(university.getTotalStudentCount());

        } else if (method.equals("getHardestCourse")) {
            System.out.println(university.getHardestCourse());

        } else if (method.equals("groupByDifficulty")) {
            university.groupByDifficulty().forEach((k, v) -> {
                System.out.printf("Difficulty: %d%n", k);
                v.forEach(System.out::println);
            });

        } else if (method.equals("getCourseEnrollmentMap")) {
            university.getCourseEnrollmentMap().forEach((k, v) -> System.out.printf("%s -> %d%n", k, v));

        } else if (method.equals("getAverageEnrollmentPerCourse")) {
            System.out.println(university.getAverageEnrollmentPerCourse());

        } else if (method.equals("getSortedCourseCodes")) {
            university.getSortedCourseCodes().forEach(System.out::println);

        } else if (method.equals("getDepartmentToCourseNames")) {
            university.getDepartmentToCourseNames().forEach((k, v) -> {
                System.out.printf("Department: %s%n", k);
                v.forEach(System.out::println);
            });

        } else if (method.equals("getAllCourses")) {
            university.getAllCourses().forEach(System.out::println);

        } else if (method.equals("getMostPopularDepartment")) {
            System.out.println(university.getMostPopularDepartment());

        } else if (method.equals("getStudentsByDifficulty")) {
            university.getStudentsByDifficulty().forEach((k, v) -> System.out.printf("%s -> %d%n", k, v));

        } else if (method.equals("getCoursesByDifficultyRange")) {
            int min = sc.nextInt();
            int max = sc.nextInt();
            university.getCoursesByDifficultyRange(min, max).forEach(System.out::println);

        } else if (method.equals("getPopularCourseCodes")) {
            int minStudents = sc.nextInt();
            university.getPopularCourseCodes(minStudents).forEach(System.out::println);

        } else if (method.equals("getTotalCreditsPerDepartment")) {
            university.getTotalCreditsPerDepartment().forEach((k, v) -> System.out.printf("%s -> %d%n", k, v));

        } else if (method.equals("getTop3HardestCourses")) {
            university.getTop3HardestCourses().forEach(System.out::println);

        } else if (method.equals("getAverageDifficultyPerDepartment")) {
            university.getAverageDifficultyPerDepartment().forEach((k, v) -> System.out.printf("%s -> %.2f%n", k, v));

        } else if (method.equals("getEnrollmentStatistics")) {
            System.out.println(university.getEnrollmentStatistics());

        } else if (method.equals("mergeFourSmallestDepartments")) {
            University updated = university.mergeFourSmallestDepartments();
            System.out.println("Merged University Departments:");
            updated.getDepartmentToCourseNames().forEach((k, v) -> {
                System.out.printf("Department: %s%n", k);
                v.forEach(System.out::println);
            });

        } else if (method.equals("getTotalCredits")) {
            System.out.println(university.getTotalCredits());

        } else if (method.equals("getDepartmentsWithAtLeastOneHardCourse")) {
            System.out.println(university.getDepartmentsWithAtLeastOneHardCourse(sc.nextInt()));

        } else if (method.equals("getCoursesWithNameContaining")) {
            System.out.println(university.getCoursesWithNameContaining(sc.nextLine()));

        } else if (method.equals("getTopDepartmentByAverageDifficulty")) {
            System.out.println(university.getTopDepartmentByAverageDifficulty());
        } else {
            System.out.println("Unknown method!");
        }
    }
}
