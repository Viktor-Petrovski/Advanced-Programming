import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class BankBalance {

    // Shared bank account
    public static class BankAccount {
        private int balance;
        private final ReentrantLock lock;
        // мора менувањето на салдото да биде извршена од макс 1 mf во дадено време

        public BankAccount(int initialBalance) {
            this.balance = initialBalance;
            lock = new ReentrantLock();
        }

        public boolean deposit(int amount) {
            lock.lock();
            try {
                balance += amount;
                return true;
            } finally {
                lock.unlock();
            }
        }

        public boolean withdraw(int amount) {
            lock.lock();
            try {
                if (balance >= amount) {
                    balance -= amount;
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        public int getBalance() {
            lock.lock();
            try {
                return balance;
            } finally {
                lock.unlock();
            }
        }
    }

    // Operation result
    public static class OperationResult {
        public final int operationId;
        public final boolean success;

        public OperationResult(int operationId, boolean success) {
            this.operationId = operationId;
            this.success = success;
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int initialBalance = sc.nextInt();
        int n = sc.nextInt(); // number of operations

        BankAccount account = new BankAccount(initialBalance);

        List<Callable<OperationResult>> tasks = new ArrayList<>();

        long lockTimeoutMs = 100; // max time to wait for the lock

        for (int i = 0; i < n; i++) {
            String type = sc.next();
            int amount = sc.nextInt();
            int operationId = i + 1;

            tasks.add(() -> {
                Thread.sleep(300);
                boolean success = type.equals("deposit") ?
                        account.deposit(amount) : account.withdraw(amount);
                return new OperationResult(operationId, success);
            });
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);

        List<Future<OperationResult>> futures = executor.invokeAll(tasks);

        List<OperationResult> results = new ArrayList<>();
        for (Future<OperationResult> f : futures)
            results.add(f.get());

        executor.shutdown();

        // Deterministic final balance
        System.out.println("FINAL_BALANCE " + account.getBalance());
    }

}
