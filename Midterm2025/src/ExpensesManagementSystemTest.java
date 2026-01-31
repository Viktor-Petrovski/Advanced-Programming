import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ExpensesManagementSystemTest {

    enum Level {
        IC, //individual contributor
        M, //middle management
        C //C-Level executives
    }

    static class Employee {
        String name;
        String jobTitle;
        Level level;

        public Employee(String name, String jobTitle, Level level) {
            this.name = name;
            this.jobTitle = jobTitle;
            this.level = level;
        }

        @Override
        public String toString() {
            return String.format(
                    "Employee: name=%s, title=%s, level=%s",
                    name,
                    jobTitle,
                    level.toString()
            );
        }


        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Employee employee = (Employee) o;
            return Objects.equals(name, employee.name) && Objects.equals(jobTitle, employee.jobTitle) && level == employee.level;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, jobTitle, level);
        }
    }

    static class Item {
        String name;
        String category;
        double price;

        public Item(String name, String category, double price) {
            this.name = name;
            this.category = category;
            this.price = price;
        }

        @Override
        public String toString() {
            return String.format("%s - %s - %.2f USD", name, category, price);
        }

    }


    static class Receipt {
        String merchant;
        LocalDateTime date;
        List<Item> items;

        public Receipt(String merchant, LocalDateTime date, List<Item> items) {
            this.merchant = merchant;
            this.date = date;
            this.items = items;
        }

        double totalAmount() {
            return items.stream().mapToDouble(i -> i.price).sum();
        }

        @Override
        public String toString() {
            String itemsStr = items.stream()
                    .map(Item::toString)
                    .collect(Collectors.joining("; "));

            return String.format(
                    "Receipt: merchant=%s, date=%s, items=%s, total=%.2f USD",
                    merchant,
                    date,
                    itemsStr,
                    totalAmount()
            );
        }

    }

    static class DailyExpensesPerCountry {
        static Map<String, Double> ALLOWANCE = new HashMap<>();

        static {
            ALLOWANCE.put("US", 50.0);
            ALLOWANCE.put("MK", 10.0);
            ALLOWANCE.put("PT", 30.0);

            ALLOWANCE.put("DE", 45.0);   // Germany
            ALLOWANCE.put("AT", 40.0);   // Austria
            ALLOWANCE.put("CH", 55.0);   // Switzerland
            ALLOWANCE.put("FR", 50.0);   // France
            ALLOWANCE.put("IT", 40.0);   // Italy
            ALLOWANCE.put("ES", 35.0);   // Spain
            ALLOWANCE.put("UK", 50.0);   // United Kingdom
            ALLOWANCE.put("NL", 45.0);   // Netherlands
            ALLOWANCE.put("BE", 45.0);   // Belgium
            ALLOWANCE.put("SE", 50.0);   // Sweden
            ALLOWANCE.put("NO", 55.0);   // Norway
            ALLOWANCE.put("DK", 50.0);   // Denmark
            ALLOWANCE.put("PL", 25.0);   // Poland
            ALLOWANCE.put("CZ", 25.0);   // Czech Republic
            ALLOWANCE.put("SK", 20.0);   // Slovakia
            ALLOWANCE.put("HU", 20.0);   // Hungary
            ALLOWANCE.put("HR", 25.0);   // Croatia
            ALLOWANCE.put("BG", 20.0);   // Bulgaria
            ALLOWANCE.put("RO", 20.0);   // Romania
            ALLOWANCE.put("GR", 30.0);   // Greece
            ALLOWANCE.put("RS", 15.0);   // Serbia
            ALLOWANCE.put("AL", 15.0);   // Albania
            ALLOWANCE.put("TR", 20.0);   // Türkiye

            ALLOWANCE.put("CA", 45.0);   // Canada
            ALLOWANCE.put("MX", 25.0);   // Mexico
            ALLOWANCE.put("BR", 20.0);   // Brazil
            ALLOWANCE.put("AR", 18.0);   // Argentina
            ALLOWANCE.put("CL", 22.0);   // Chile

            ALLOWANCE.put("AU", 50.0);   // Australia
            ALLOWANCE.put("NZ", 40.0);   // New Zealand

            ALLOWANCE.put("JP", 45.0);   // Japan
            ALLOWANCE.put("CN", 30.0);   // China
            ALLOWANCE.put("KR", 35.0);   // South Korea
            ALLOWANCE.put("SG", 50.0);   // Singapore
            ALLOWANCE.put("IN", 20.0);   // India
            ALLOWANCE.put("AE", 45.0);   // UAE (Dubai)
            ALLOWANCE.put("SA", 30.0);   // Saudi Arabia
        }
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        float maxReceiptAmount = Float.parseFloat(sc.nextLine());

        // Create system with some default max amount
        ExpenseManagementSystem system = new ExpenseManagementSystem(maxReceiptAmount);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("END")) break;
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            String method = parts[0];


            switch (method) {

                case "addReceiptExpense": {
                    // Format:
                    // addReceiptExpense;Name;Job;IC|M|C;description;amount;merchant;datetime;item|cat|price,...
                    String empName = parts[1];
                    String job = parts[2];
                    Level lvl = Level.valueOf(parts[3]);
                    String description = parts[4];

                    String merchant = parts[5];
                    LocalDateTime dt = LocalDateTime.parse(parts[6]);

                    // Items list
                    String itemsRaw = parts[7];
                    List<Item> items = new ArrayList<>();
                    if (!itemsRaw.equalsIgnoreCase("none")) {
                        for (String itemStr : itemsRaw.split(",")) {
                            String[] ip = itemStr.split("\\|");
                            items.add(new Item(ip[0], ip[1], Double.parseDouble(ip[2])));
                        }
                    }

                    Employee e = new Employee(empName, job, lvl);
                    Receipt r = new Receipt(merchant, dt, items);

                    try {
                        system.addReceiptExpense(e, description, r);
                    } catch (NotSupportedExpenseException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                }

                case "addTravelExpense": {
                    // Format:
                    // addTravelExpense;Name;Job;IC|M|C;description;amount;start;end;country
                    String empName = parts[1];
                    String job = parts[2];
                    Level lvl = Level.valueOf(parts[3]);
                    String description = parts[4];
                    double amount = Double.parseDouble(parts[5]);
                    LocalDateTime start = LocalDateTime.parse(parts[6]);
                    LocalDateTime end = LocalDateTime.parse(parts[7]);
                    String country = parts[8];

                    Employee e = new Employee(empName, job, lvl);

                    try {
                        system.addTravelExpense(e, description, amount, start, end, country);
                    } catch (NotSupportedExpenseException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                }

                case "printRefunds": {
                    system.printRefunds();
                    break;
                }

                case "totalRefundsPerEmployee": {
                    Map<Employee, Double> map = system.totalRefundsPerEmployee();
                    map.forEach((emp, total) ->
                            System.out.printf("%s -> %.2f%n", emp.name, total));
                    break;
                }

                default:
                    System.out.println("Unknown method: " + method);
            }
        }
    }

    static class NotSupportedExpenseException extends Exception {
        public NotSupportedExpenseException(String message) {
            super(message);
        }
    }

    static abstract class Expense {
        protected final Employee employee;
        protected final String reason;

        Expense(Employee employee, String reason) {
            this.employee = employee;
            this.reason = reason;
        }

        abstract boolean overlaps(LocalDateTime ldt);

        abstract double getRefund();
    }

    static class TravelExpense extends Expense {
        private final double amount;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final String country;

        TravelExpense(Employee employee, String reason, double amount, LocalDateTime start, LocalDateTime end, String country) {
            super(employee, reason);
            this.amount = amount;
            this.start = start;
            this.end = end;
            this.country = country;
        }

        @Override
        boolean overlaps(LocalDateTime ldt) {
            return ldt.isAfter(start) && ldt.isBefore(end);
        }

        /// За патни трошоци: На вработениот му следува рефундирање на целиот износ пријавен за трошокот плус дневница за секој
        /// поминат ден во државата согласно дневниците дефинирани во мапата DailyExpensesPerCountry.ALLOWANCE
        @Override
        double getRefund() {
            long days = Duration.between(start, end).toDays();
            double allowance = DailyExpensesPerCountry.ALLOWANCE.get(country);
            return amount + (allowance * days);
        }

        @Override
        public String toString() {
            return "TravelExpense: " +
                    "employee={" + employee +
                    "}, description=" + reason +
                    String.format(", baseAmount=%.2f USD", amount) +
                    ", country=" + country +
                    ", start=" + start +
                    ", end=" + end +
                    String.format(", refund=%.2f USD", getRefund());

        }
    }

    static class ReceiptExpense extends Expense {
        private final Receipt receipt;

        ReceiptExpense(Employee employee, String reason, Receipt receipt) {
            super(employee, reason);
            this.receipt = receipt;
        }

        @Override
        boolean overlaps(LocalDateTime ldt) {
            // always false because of business logic
            return false;
        }

        /// За трошоци направени со фискална сметка: Компанијата ќе ги прегледа сите ставки од фискалната сметка и
        /// ќе ги рефундира само ставките од категориите за кои вработениот има право да прави трошоци.
        ///
        /// Вработените од ниво IC имаат право да купуваат само ставки од категориите food и non-alcohol beverage.
        ///
        /// Вработените од ниво M имаат право и на ставки од категориите transport и alcohol beverage.
        ///
        /// Вработените од ниво C немаат ограничувања на категории
        @Override
        double getRefund() {
            return receipt.items.stream()
                    .filter(this::isItemRefundable)
                    .mapToDouble(i -> i.price).sum();
        }

        private boolean isItemRefundable(Item i) {
            if (employee.level.equals(Level.IC))
                return List.of("food", "non-alcohol beverage").contains(i.category);
            if (employee.level.equals(Level.M))
                return List.of("food", "non-alcohol beverage", "transport", "alcohol beverage").contains(i.category);
            return employee.level.equals(Level.C);
        }

        @Override
        public String toString() {
            return "ReceiptExpense: " +
                    "employee={" + employee +
                    "}, description=" + reason +
                    String.format(", receiptAmount=%.2f USD", receipt.totalAmount()) +
                    ", receiptDate=" + receipt.date +
                    ", itemsCount=" + receipt.items.size() +
                    String.format(", refund=%.2f USD", getRefund());

        }

    }

    static class ExpenseManagementSystem {
        private final float maxReceiptAmount;
        private final Map<Employee, Set<Expense>> employeeExpenseMap;

        ExpenseManagementSystem(float maxReceiptAmount) {
            this.maxReceiptAmount = maxReceiptAmount;
            employeeExpenseMap = new HashMap<>();
        }

        /// метод за додавање на трошоци за службено патување со авион на вработениот employee со причина за патување reason,
        /// со износ на трошоците (пр. цена на авио билет) amount, во периодот од start до end во држвата country
        void addTravelExpense(Employee employee, String reason, double amount, LocalDateTime start, LocalDateTime end, String country)
                throws NotSupportedExpenseException {

            Expense ins = new TravelExpense(employee, reason, amount, start, end, country);
            employeeExpenseMap.computeIfAbsent(employee, k -> new LinkedHashSet<>()).add(ins);
        }

        /// метод за додавање на службен трошок направен од вработениот employee со причината reason
        ///  ((пр. ручек за вработените во ресторан), со фискална сметка receipt.
        ///
        /// Со исклучок од тип NotSupportedExpenseException, да се спречи додавање на трошок кој е направен
        /// за време на службено патување на вработениот (за кое веќе му следува дневница) во периодот додека веќе има одобрен трошок за патување.
        ///
        /// Исто со исклучок од тип NotSupportedExpenseException, да се спречи додавање на трошок кој го надминува
        /// максимално дозволениот износ на трошок направен со фискална сметка
        void addReceiptExpense(Employee employee, String reason, Receipt receipt) throws NotSupportedExpenseException {
            Set<Expense> expenseSet = employeeExpenseMap.computeIfAbsent(employee, k -> new LinkedHashSet<>());

            if (expenseSet.stream().anyMatch(e -> e.overlaps(receipt.date)))
                throw new NotSupportedExpenseException("You cannot add receipt expense in the same period during an approved travel expense.");

            if (receipt.totalAmount() > maxReceiptAmount)
                throw new NotSupportedExpenseException(
                        String.format("Receipt with amount %.2f exceeds the max allowed amount for receipt expense %.2f",
                                receipt.totalAmount(), maxReceiptAmount)
                );

            expenseSet.add(new ReceiptExpense(employee, reason, receipt));
        }

        /// метод кој ги печати трошоците на вработените во форматот како во тест примерите,
        /// подредени според износот кој компанијата ќе го рефундира за реализираниот трошок, во опаѓачки редослед.
        /// Правилата за рефундација на трошоци се следни:
        void printRefunds() {
            employeeExpenseMap.values().stream().flatMap(Collection::stream)
                    .sorted(Comparator.comparing(Expense::getRefund).reversed())
                    .forEach(System.out::println);
        }

        private double totalRefund(Map.Entry<Employee, Set<Expense>> e) {
            return e.getValue().stream().mapToDouble(Expense::getRefund).sum();
        }

        /// метод кој враќа мапа во која клучеви се сите вработени,
        /// а вредности се вкупните износи кои им се исплатени за рефундација на трошоци на соодветните вработени.
        Map<Employee, Double> totalRefundsPerEmployee() {
            return employeeExpenseMap.entrySet().stream()
                    .sorted(Comparator.comparing(this::totalRefund).reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey, this::totalRefund,
                            (x, y) -> x,
                            LinkedHashMap::new
                    ));
        }
    }
}
//Hint: Use Duration.betweеn(..).toDays() за да се пресмета растојание во денови меѓу два датуми.