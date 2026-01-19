import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class DiscountsTest {
    public static void main(String[] args) {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores();
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::print);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::print);
    }

    static class Price implements Comparable<Price> {
        final int discount;
        final int regular;

        Price(int discount, int regular) {
            this.discount = discount;
            this.regular = regular;
        }

        int percentDiscount() {
            return (int)(100 - 100 * ((discount * 1.0) / regular));
        }

        int absoluteDiscount() {
            return regular - discount;
        }

        @Override
        public int compareTo(Price o) {
            int i = Integer.compare(o.percentDiscount(), this.percentDiscount());
            if (i != 0) return i;
            i = Integer.compare(o.absoluteDiscount(), this.absoluteDiscount());
            if (i != 0) return i;
            return Integer.compare(o.discount, discount);
        }

        @Override
        public String toString() {
            return String.format("%2d%% %d/%d\n", percentDiscount(), discount, regular);
        }
    }

    static class Store {
        private final String name;
        private final Set<Price> priceSet;

        Store(String s) {
            String[] tokens = s.split("\\s+");
            name = tokens[0];

            priceSet = new TreeSet<>();
            for (int i = 1; i < tokens.length; i++) {
                String[] p = tokens[i].split(":");
                priceSet.add(new Price(Integer.parseInt(p[0]), Integer.parseInt(p[1])));
            }
        }

        double avgDiscount() {
            return priceSet.stream().mapToDouble(Price::percentDiscount).average().orElse(.0);
        }

        int totalDiscount() {
            return priceSet.stream().mapToInt(Price::absoluteDiscount).sum();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(String.format("\nAverage discount: %.1f%%\n", avgDiscount()));
            sb.append(String.format("Total discount: %d\n", totalDiscount()));

            priceSet.forEach(sb::append);
            return sb.toString();
        }


        public static final Comparator<Store> BY_NAME = Comparator.comparing(s -> s.name);
        public static final Comparator<Store> BY_AVG = Comparator.comparing(Store::avgDiscount).reversed();
        public static final Comparator<Store> BY_TOTAL = Comparator.comparing(Store::totalDiscount);
    }

    static class Discounts {
        private final static int LIMIT = 3;
        private final List<Store> storeList;

        Discounts() {
            storeList = new ArrayList<>();
        }

        public int readStores() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().forEach(l -> storeList.add(new Store(l)));
            return storeList.size();
        }

        public List<Store> byAverageDiscount() {
            return storeList.stream()
                    .sorted(Store.BY_AVG.thenComparing(Store.BY_NAME))
                    .limit(LIMIT)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public List<Store> byTotalDiscount() {
            return storeList.stream()
                    .sorted(Store.BY_TOTAL.thenComparing(Store.BY_NAME))
                    .limit(LIMIT)
                    .collect(Collectors.toCollection(ArrayList::new));

        }
    }
}

