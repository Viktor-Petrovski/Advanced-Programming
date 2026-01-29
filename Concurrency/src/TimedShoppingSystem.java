import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class TimedShoppingSystem {

    // ------------------------------------------------------
    // Domain classes
    // ------------------------------------------------------

    static class Item {
        private final String id;
        private final AtomicInteger quantity;

        public Item(String id, int quantity) {
            this.id = id;
            this.quantity = new AtomicInteger(quantity);
        }

        public String getId() {
            return id;
        }

        public int getQuantity() {
            return quantity.get();
        }

        /**
         * Attempts to reserve the given amount.
         * Must be atomic.
         */
        public boolean reserve(int amount) {
            while (true) {
                int current = quantity.get();
                if (current < amount)
                    return false;

                int updated = current - amount;
                if (quantity.compareAndSet(current, updated))
                    return true;
            }
        }

        /**
         * Returns quantity back to the item.
         */
        public void release(int amount) {
            quantity.addAndGet(amount);
        }

        @Override
        public String toString() {
            return "Item{" +
                    "id='" + id + '\'' +
                    ", quantity=" + quantity.get() +
                    '}';
        }
    }

    static class Catalogue {
        private final Map<String, Item> items = new HashMap<>();

        public void addItem(Item item) {
            items.put(item.getId(), item);
        }

        public Item getItem(String id) {
            return items.get(id);
        }
    }

    static class Basket {
        private final String id;
        private final ConcurrentMap<Item, Integer> reserved = new ConcurrentHashMap<>();
        private boolean checkedOut = false;

        private final ReentrantLock lock = new ReentrantLock(false);

        public Basket(String id) {
            this.id = id;
        }

        /**
         * Adds an item to this basket.
         * Must interact safely with the catalogue.
         */
        public void addItem(Item item, int amount) {
            if (item.reserve(amount))
                reserved.merge(item, amount, Integer::sum);
        }

        /**
         * Marks the basket as checked out.
         */
        public void checkout() {
            lock.lock();
            try {
                checkedOut = true;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Cleans the basket and returns reserved quantities.
         * Must be safe to call concurrently with checkout.
         */
        public void cleanup() {
            lock.lock();
            try {
                if (!checkedOut) {
                    reserved.forEach(Item::release);
                    reserved.clear();
                }
            } finally {
                lock.unlock();
            }
        }

        @Override
        public String toString() {
            return "Basket{" +
                    "id='" + id + '\'' +
                    ", reserved=" + reserved +
                    ", checkedOut=" + checkedOut +
                    '}';
        }
    }

    // ------------------------------------------------------
    // Manager
    // ------------------------------------------------------

    static class BasketManager {
        private final ScheduledExecutorService scheduler;
        private final int basketTtlSeconds;

        public BasketManager(int basketTtlSeconds) {
            this.basketTtlSeconds = basketTtlSeconds;
            this.scheduler = Executors.newScheduledThreadPool(1);
        }

        /**
         * Creates a new basket and schedules its cleanup.
         */
        public Basket createBasket(String id) {
            Basket basket = new Basket(id);
            scheduler.schedule(basket::cleanup, basketTtlSeconds, TimeUnit.SECONDS);

            return basket;
        }

        /**
         * Initiates shutdown.
         */
        public void shutdown() throws InterruptedException {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS))
                scheduler.shutdownNow();
        }
    }

    // ------------------------------------------------------
    // Tester main
    // ------------------------------------------------------

    public static void main(String[] args) throws Exception {
        Catalogue catalogue = new Catalogue();
        catalogue.addItem(new Item("apple", 5));
        catalogue.addItem(new Item("banana", 3));
        catalogue.addItem(new Item("orange", 4));

        BasketManager manager = new BasketManager(3);

        // Create 3 baskets
        Basket basket1 = manager.createBasket("B1");
        Basket basket2 = manager.createBasket("B2");
        Basket basket3 = manager.createBasket("B3");

        // Basket 1 & 2 simulate concurrent clients
        Runnable client1 = () -> {
            basket1.addItem(catalogue.getItem("apple"), 2);
            basket1.addItem(catalogue.getItem("banana"), 1);
        };

        Runnable client2 = () -> {
            basket2.addItem(catalogue.getItem("apple"), 3);
            basket2.addItem(catalogue.getItem("orange"), 2);
        };

        // Basket 3 tests checkout: reserved items should not return to catalogue
        Runnable client3 = () -> {
            basket3.addItem(catalogue.getItem("banana"), 2);
            basket3.addItem(catalogue.getItem("orange"), 1);
            basket3.checkout(); // mark as permanently taken
        };

        Thread t1 = new Thread(client1);
        Thread t2 = new Thread(client2);
        Thread t3 = new Thread(client3);

        t1.start(); t2.start(); t3.start();
        t1.join(); t2.join(); t3.join();

        System.out.println("Waiting for baskets 1 & 2 to expire...\n");
        Thread.sleep(5000);

        System.out.println("Final catalogue quantities:");
        System.out.println("Apple: " + catalogue.getItem("apple").getQuantity());
        System.out.println("Banana: " + catalogue.getItem("banana").getQuantity());
        System.out.println("Orange: " + catalogue.getItem("orange").getQuantity());

        manager.shutdown();

        System.out.println(basket1);
        System.out.println(basket2);
        System.out.println(basket3);
    }

}