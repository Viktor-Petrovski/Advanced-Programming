# Timed Shopping Basket – Concurrency Exam Problem

## Context

You are implementing the core logic of an **online shopping system**.

The system consists of a **Catalogue** of items and multiple **client baskets**. Each item in the catalogue has a **limited quantity**. Multiple clients may attempt to add the same item to their basket concurrently.

When an item is added to a basket, the quantity is **temporarily reserved**. If the basket is **not checked out within a fixed time window**, the reservation expires and the quantity must be **returned to the catalogue automatically**.

This problem focuses on **correct synchronization, time-based coordination, and consistency under concurrency**.

---

## Entities

### 1. Item

Represents a product in the catalogue.

* Has an ID and available quantity
* Quantity updates must be **atomic**
* Cannot go below zero

### 2. Catalogue

Holds all items.

* Shared across all clients
* Multiple threads may access or modify item quantities concurrently

### 3. Basket

Represents a temporary shopping basket.

* Contains reserved items and quantities
* Reservations are **time-limited**
* Must be cleaned automatically after expiration

### 4. BasketManager

Central coordinator.

* Creates baskets
* Handles item reservation
* Schedules basket cleanup
* Ensures expired reservations are released correctly

---

## Functional Requirements

### 1. Atomic Reservation

* Adding an item to a basket must:

    * Check availability
    * Reduce catalogue quantity atomically
* Partial reservations are not allowed

### 2. Concurrency Safety

* Multiple clients may:

    * Add the same item concurrently
    * Create baskets concurrently
* No race conditions or lost updates are allowed

### 3. Basket Expiration

* Each basket has a **time-to-live (TTL)** in seconds
* After TTL expires:

    * Basket is automatically cleaned
    * All reserved quantities are returned to the catalogue

### 4. Checkout vs Cleanup

* If a basket is checked out before expiration:

    * Items are permanently removed
    * Cleanup must not return quantities

### 5. Shutdown Semantics

* The system may be shut down
* No new baskets may be created after shutdown
* Pending cleanups must complete safely

---

## What Is Being Tested

* Atomic updates on shared state
* Correct use of blocking and coordination (without semaphores)
* Time-based task scheduling
* Safe cleanup logic
* Reasoning about lifecycle and ownership

---

## Starter Code (Java)

```java
import java.util.*;
import java.util.concurrent.*;

public class TimedShoppingSystem {

    // ------------------------------------------------------
    // Domain classes
    // ------------------------------------------------------

    static class Item {
        private final String id;
        private int quantity;

        public Item(String id, int quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        public String getId() {
            return id;
        }

        public int getQuantity() {
            return quantity;
        }

        /**
         * Attempts to reserve the given amount.
         * Must be atomic.
         */
        public boolean reserve(int amount) {
            // TODO
            return false;
        }

        /**
         * Returns quantity back to the item.
         */
        public void release(int amount) {
            // TODO
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
        private final Map<Item, Integer> reserved = new HashMap<>();
        private boolean checkedOut = false;

        public Basket(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        /**
         * Adds an item to this basket.
         * Must interact safely with the catalogue.
         */
        public void addItem(Item item, int amount) {
            // TODO
        }

        /**
         * Marks the basket as checked out.
         */
        public void checkout() {
            // TODO
        }

        /**
         * Cleans the basket and returns reserved quantities.
         * Must be safe to call concurrently with checkout.
         */
        public void cleanup() {
            // TODO
        }
    }

    // ------------------------------------------------------
    // Manager
    // ------------------------------------------------------

    static class BasketManager {
        private final Catalogue catalogue;
        private final ScheduledExecutorService scheduler;
        private final int basketTtlSeconds;
        private volatile boolean shutdown = false;

        public BasketManager(Catalogue catalogue, int basketTtlSeconds) {
            this.catalogue = catalogue;
            this.basketTtlSeconds = basketTtlSeconds;
            this.scheduler = Executors.newScheduledThreadPool(1);
        }

        /**
         * Creates a new basket and schedules its cleanup.
         */
        public Basket createBasket(String id) {
            // TODO
            return null;
        }

        /**
         * Initiates shutdown.
         */
        public void shutdown() {
            // TODO
        }
    }

    // ------------------------------------------------------
    // Tester main
    // ------------------------------------------------------

    public static void main(String[] args) throws Exception {
        Catalogue catalogue = new Catalogue();
        catalogue.addItem(new Item("apple", 5));

        BasketManager manager = new BasketManager(catalogue, 3);

        Basket basket = manager.createBasket("B1");

        new Thread(() -> basket.addItem(catalogue.getItem("apple"), 3)).start();
        new Thread(() -> basket.addItem(catalogue.getItem("apple"), 3)).start();

        Thread.sleep(5000);

        System.out.println("Final quantity of apple: " + catalogue.getItem("apple").getQuantity());

        manager.shutdown();
    }
}
```

---

## Notes

* Incorrect atomicity will oversell items
* Missing synchronization will cause lost returns
* Incorrect cleanup logic will duplicate inventory
* A naive solution may pass light tests but fail under contention

---

Good luck.
