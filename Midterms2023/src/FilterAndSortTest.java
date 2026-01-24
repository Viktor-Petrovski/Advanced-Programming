import javax.swing.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterAndSortTest {

    static class Student implements Comparable<Student> {
        String id;
        List<Integer> grades;

        public Student(String id, List<Integer> grades) {
            this.id = id;
            this.grades = grades;
        }

        public double average() {
            return grades.stream().mapToDouble(i -> i).average().orElse(.0);
        }

        public int getYear() {
            return (24 - Integer.parseInt(id.substring(0, 2)));
        }

        public int totalCourses() {
            return Math.min(getYear() * 10, 40);
        }

        public double labAssistantPoints() {
            return average() * ((double) grades.size() / totalCourses()) * (0.8 + ((getYear()-1)*0.2)/3.0);
        }

        @Override
        public int compareTo(Student o) {
            return Comparator.comparing(Student::labAssistantPoints)
                    .thenComparing(Student::average)
                    .compare(this, o);
        }

        @Override
        public String toString() {
            return String.format("Student %s (%d year) - %d/%d passed exam, average grade %.2f.\nLab assistant points: %.2f",
                    id, getYear(), grades.size(), totalCourses(), average(), labAssistantPoints());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = Integer.parseInt(sc.nextLine());
        int n = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { // students
            int studentScenario = Integer.parseInt(sc.nextLine());
            List<Student> students = new ArrayList<>();
            while (n > 0) {

                String line = sc.nextLine();
                String[] parts = line.split("\\s+");
                String id = parts[0];
                List<Integer> grades = Arrays.stream(parts).skip(1).map(Integer::parseInt).collect(Collectors.toList());
                students.add(new Student(id, grades));
                --n;
            }

            if (studentScenario == 1) {
                try {
                    Predicate<Student> condition = s -> s.labAssistantPoints() >= 8.0 && s.getYear() >= 3;
                    List<Student> res = FilterAndSort.execute(students, condition);
                    res.forEach(System.out::println);
                } catch (EmptyResultException e) {
                    System.out.println(e.getMessage());
                }
                //TODO filter and sort all students who have at least 8.0 points and are at least 3rd year student

            } else {
                //TODO filter and sort all students who have passed at least 90% of their total courses with an average grade of at least 9.0
                try {
                    Predicate<Student> condition = s -> s.grades.size() / (s.totalCourses() * 1.0) > .9 && s.average() >= 9.0;
                    List<Student> res = FilterAndSort.execute(students, condition);
                    res.forEach(System.out::println);
                } catch (EmptyResultException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else { //integers
            List<Integer> integers = new ArrayList<>();
            while (n > 0) {
                integers.add(Integer.parseInt(sc.nextLine()));
                --n;
            }

            try {
                Predicate<Integer> condition = i -> i % 15 == 0;
                List<Integer> res = FilterAndSort.execute(integers, condition);
                res.forEach(System.out::println);
            } catch (EmptyResultException e) {
                System.out.println(e.getMessage());
            }


            //TODO filter and sort all even numbers divisible with 15

        }

    }

    static class EmptyResultException extends Exception {
        public EmptyResultException() {
            super("No element met the criteria");
        }
    }

    static class FilterAndSort {
        static <T extends Comparable<T>> List<T> execute(List<T> list, Predicate<T> predicate) throws EmptyResultException {
            List<T> sorted = list.stream().filter(predicate).sorted(Comparator.reverseOrder())
                    .collect(Collectors.toCollection(ArrayList::new));

            if (sorted.isEmpty())
                throw new EmptyResultException();
            return sorted;
        }
    }

}
