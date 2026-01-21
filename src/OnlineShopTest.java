import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OnlineShopTest {

    public static void main(String[] args) {
        OnlineShop onlineShop = new OnlineShop();
        double totalAmount = 0.0;
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equalsIgnoreCase("addproduct")) {
                String category = parts[1];
                String id = parts[2];
                String name = parts[3];
                LocalDateTime createdAt = LocalDateTime.parse(parts[4]);
                double price = Double.parseDouble(parts[5]);
                onlineShop.addProduct(category, id, name, createdAt, price);
            } else if (parts[0].equalsIgnoreCase("buyproduct")) {
                String id = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                try {
                    totalAmount += onlineShop.buyProduct(id, quantity);
                } catch (ProductNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String category = parts[1];
                if (category.equalsIgnoreCase("null"))
                    category=null;
                String comparatorString = parts[2];
                int pageSize = Integer.parseInt(parts[3]);
                COMPARATOR_TYPE comparatorType = COMPARATOR_TYPE.valueOf(comparatorString);
                printPages(onlineShop.listProducts(category, comparatorType, pageSize));
            }
        }
        System.out.println("Total revenue of the online shop is: " + totalAmount);

    }

    private static void printPages(List<List<Product>> listProducts) {
        for (int i = 0; i < listProducts.size(); i++) {
            System.out.println("PAGE " + (i + 1));
            listProducts.get(i).forEach(System.out::println);
        }
    }

    enum COMPARATOR_TYPE {
        NEWEST_FIRST,
        OLDEST_FIRST,
        LOWEST_PRICE_FIRST,
        HIGHEST_PRICE_FIRST,
        MOST_SOLD_FIRST,
        LEAST_SOLD_FIRST
    }

    static class ProductNotFoundException extends Exception {
        ProductNotFoundException(String id) {
            super(String.format("Product with id %s does not exist in the online shop!", id));
        }
    }


    static class Product {
        private final String category;
        private final String id;
        private final String name;
        private final LocalDateTime createdAt;
        private final double price;
        private long quantitySold;

        Product(String category, String id, String name, LocalDateTime createdAt, double price) {
            this.category = category;
            this.id = id;
            this.name = name;
            this.createdAt = createdAt;
            this.price = price;

            quantitySold = 0;
        }

        public String getCategory() {
            return category;
        }

        double buy(int quantity) {
            quantitySold += quantity;
            return quantity * price;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", createdAt=" + createdAt +
                    ", price=" + price +
                    ", quantitySold=" + quantitySold +
                    '}';
        }

        /*
        NEWEST_FIRST,
        OLDEST_FIRST,
        LOWEST_PRICE_FIRST,
        HIGHEST_PRICE_FIRST,
        MOST_SOLD_FIRST,
        LEAST_SOLD_FIRST
         */

        public static final Comparator<Product> OLDEST_FIRST = Comparator.comparing(p -> p.createdAt);
        public static final Comparator<Product> LOWEST_PRICE_FIRST = Comparator.comparing(p -> p.price);
        public static final Comparator<Product> LEAST_SOLD_FIRST = Comparator.comparing(p -> p.quantitySold);
    }

    static class OnlineShop {
        private final Map<String, Product> productMap; // id -> product

        OnlineShop() {
            productMap = new HashMap<>();
        }

        void addProduct(String category, String id, String name, LocalDateTime createdAt, double price){
            productMap.put(id, new Product(category, id, name, createdAt, price));
        }

        double buyProduct(String id, int quantity) throws ProductNotFoundException{
            if (!productMap.containsKey(id))
                throw new ProductNotFoundException(id);
            return productMap.get(id).buy(quantity);
        }

        private Comparator<Product> getComparator(COMPARATOR_TYPE comparatorType) {
            if (comparatorType.equals(COMPARATOR_TYPE.NEWEST_FIRST))
                return Product.OLDEST_FIRST.reversed();
            if (comparatorType.equals(COMPARATOR_TYPE.OLDEST_FIRST))
                return Product.OLDEST_FIRST;
            if (comparatorType.equals(COMPARATOR_TYPE.LOWEST_PRICE_FIRST))
                return Product.LOWEST_PRICE_FIRST;
            if (comparatorType.equals(COMPARATOR_TYPE.HIGHEST_PRICE_FIRST))
                return Product.LOWEST_PRICE_FIRST.reversed();
            if (comparatorType.equals(COMPARATOR_TYPE.LEAST_SOLD_FIRST))
                return Product.LEAST_SOLD_FIRST;
            //if (comparatorType.equals(COMPARATOR_TYPE.MOST_SOLD_FIRST))
            return Product.LEAST_SOLD_FIRST.reversed();
        }

        List<List<Product>> listProducts(String category, COMPARATOR_TYPE comparatorType, int pageSize) {
            Comparator<Product> comparator = getComparator(comparatorType);

            List<Product> products = productMap.values().stream()
                    .filter(p -> category == null || category.equalsIgnoreCase(p.getCategory()))
                    .sorted(comparator)
                    .collect(Collectors.toCollection(ArrayList::new));

            List<List<Product>> result = new ArrayList<>();

            int size = products.size();
            int range = size / pageSize;
            range = size % pageSize > 0 ? range + 1 : range;

            IntStream.range(0, range).forEach(i -> {
                List<Product> page = new ArrayList<>();
                IntStream.range(0, pageSize).forEach(j -> {
                    int idx = i * pageSize + j;

                    if (idx < size)
                        page.add(products.get(idx));
                });
                result.add(page);
            });

            return result;
        }

    }
}

