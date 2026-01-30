import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.IntStream;

public class OnlinePaymentsTest {
    public static void main(String[] args) {
        OnlinePayments onlinePayments = new OnlinePayments();
        onlinePayments.readItems();
        IntStream.range(151020, 151025).mapToObj(String::valueOf)
                .forEach(onlinePayments::printStudentReport);
    }

    static class Item implements Comparable<Item> {
        private final String description;
        private final int price;

        Item(String description, int price) {
            this.description = description;
            this.price = price;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return description + " " + price;
        }

        @Override
        public int compareTo(Item o) {
            int i = Integer.compare(o.price, this.price);
            return i != 0 ? i : 1;
        }
    }

    static class OnlinePayments {
        private final Map<String, Set<Item>> studentPaymentMap;

        OnlinePayments() {
            studentPaymentMap = new HashMap<>();
        }

        /// метод за вчитување на сите ставки кои се платени преку модулот.
        /// Секоја ставка е во нов ред и е во следниот формат STUDENT_IDX ITEM_NAME PRICE
        void readItems() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().forEach(l -> {
                String[] tokens = l.split(";");

                String id = tokens[0];
                Item ins = new Item(tokens[1], Integer.parseInt(tokens[2]));

                studentPaymentMap.computeIfAbsent(id, k -> new TreeSet<>()).add(ins);
            });
            // 151020;Административно-материјални трошоци и осигурување;750
            //151020;Школарина за летен семестар 2022/2023;12300
        }

        /// метод за печатење на извештај за студентот со индекс id.
        /// Во извештајот треба да се испечати нето износот на сите платени ставки, наплатената банкарска провизија,
        /// вкупниот износ кој е наплатен од студентите, како и нумерирана листа од сите ставки кои се платени од студентите
        /// сортирани во опаѓачки редослед според цената.
        ///
        /// Провизијата се пресметува врз вкупниот износ на ставките кои студентот ги плаќа и изнесува 1.14%
        /// (но најмалку 3 денари, а најмногу 300). Децималните износи се заокрузуваат со Math.round.
        void printStudentReport(String index) {
            PrintWriter pw = new PrintWriter(System.out);
            if (!studentPaymentMap.containsKey(index)) {
                pw.printf("Student %s not found!\n", index);
                pw.flush();
                return;
            }

            List<Item> items = new ArrayList<>(studentPaymentMap.get(index));
            int net = items.stream().mapToInt(Item::getPrice).sum();
            int fee = (int) Math.round(net * .0114);
            fee = Math.min(fee, 300);
            fee = Math.max(fee, 3);

            pw.println(String.format("Student: %s Net: %d Fee: %d Total: %d\nItems:",
                    index, net, fee, net + fee));

            IntStream.range(0, items.size()).forEach(i ->
                    pw.printf("%d. %s\n", i + 1, items.get(i))
            );

            pw.flush();

            //Student: 151020 Net: 13050 Fee: 149 Total: 13199
            //Items:
            //1. Школарина за летен семестар 2022/2023 12300
            //2. Административно-материјални трошоци и осигурување 750
            //Student 151021 not found!
            //Student 151022 not found!
            //Student 151023 not found!
            //Student 151024 not found!
        }
    }
}