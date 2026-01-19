import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees();

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i = 5; i <= 10; i++) {
            levels.add("level" + i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: " + level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });
    }

    static abstract class Employee implements Comparable<Employee> {
        protected final String id;
        protected final String level;
        protected final double rate;

        Employee(String id, String level, double rate) {
            this.id = id;
            this.level = level;
            this.rate = rate;
        }

        abstract double getWage();

        public String getLevel() { return level; }

        @Override
        public int compareTo(Employee o) {
            int i = Double.compare(o.getWage(), this.getWage());
            if (i != 0) return i;
            return this.id.compareTo(o.id);
        }
    }

    static class HourlyEmployee extends Employee {
        private final double hours;

        HourlyEmployee(String id, String level, double hours, double rate) {
            super(id, level, rate);
            this.hours = hours;
        }

        @Override
        double getWage() {
            if (hours <= 40)
                return hours * rate;
            return (40 * rate) + ((hours - 40) * rate * 1.5);
        }

        @Override
        public String toString() {
            return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f",
                    id, level, getWage(), Math.min(hours, 40), Math.max(0.0, hours - 40));
        }
    }

    static class FreelanceEmployee extends Employee {
        private final List<Integer> points;

        FreelanceEmployee(String id, String level, List<Integer> points, double rate) {
            super(id, level, rate);
            this.points = points;
        }

        @Override
        double getWage() {
            return points.stream().mapToInt(i -> i).sum() * rate;
        }

        @Override
        public String toString() {
            int sumPoints = points.stream().mapToInt(i -> i).sum();
            return String.format("Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d",
                    id, level, getWage(), points.size(), sumPoints);
        }
    }

    static class EmployeeFactory {
        static Employee create(String s, Map<String, Double> hourlyRates, Map<String, Double> ticketRates) {
            String info = s.substring(2);
            String[] tokens = info.split(";");
            String id = tokens[0];
            String level = tokens[1];

            if (s.startsWith("H")) {
                double hours = Double.parseDouble(tokens[2]);
                return new HourlyEmployee(id, level, hours, hourlyRates.get(level));
            } else {
                List<Integer> points = Arrays.stream(tokens).skip(2)
                        .map(Integer::parseInt).collect(Collectors.toList());
                return new FreelanceEmployee(id, level, points, ticketRates.get(level));
            }
        }
    }

    static class PayrollSystem {
        public final Map<String, Double> hourlyRateByLevel;
        public final Map<String, Double> ticketRateByLevel;

        private final Collection<Employee> employees;

        PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
            this.hourlyRateByLevel = hourlyRateByLevel;
            this.ticketRateByLevel = ticketRateByLevel;

            employees = new ArrayList<>();
        }

        void readEmployees() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().map(s -> EmployeeFactory.create(s, hourlyRateByLevel, ticketRateByLevel))
                    .forEach(employees::add);
        }

        Map<String, Set<Employee>> printEmployeesByLevels(Set<String> levels) {
            return employees.stream()
                    .filter(e -> levels.contains(e.getLevel()))
                    .collect(Collectors.groupingBy(
                            Employee::getLevel,
                            TreeMap::new, // лексикографски според клучот
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.<Employee>naturalOrder()))
                    ));
        }
    }
}