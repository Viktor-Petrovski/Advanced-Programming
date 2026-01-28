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

        public boolean deposit(int amount, long lockTimeoutMs) throws InterruptedException, TimeoutException {
            if (lock.tryLock(lockTimeoutMs, TimeUnit.MILLISECONDS))
                try {
                    balance += amount;
                    return true;
                } finally {
                    lock.unlock();
                }
            throw new TimeoutException("Could not acquire lock to deposit");
        }

        public boolean withdraw(int amount, long lockTimeoutMs) throws InterruptedException, TimeoutException {
            if (lock.tryLock(lockTimeoutMs, TimeUnit.MILLISECONDS))
                try {
                    if (balance >= amount) {
                        balance -= amount;
                        return true;
                    }
                    return false;
                } finally {
                    lock.unlock();
                }
            throw new TimeoutException("Could not acquire lock to withdraw");
        }

        public int getBalance(long lockTimeoutMs) throws InterruptedException, TimeoutException {
            if (lock.tryLock(lockTimeoutMs, TimeUnit.MILLISECONDS))
                try {
                    return balance;
                } finally {
                    lock.unlock();
                }
            throw new TimeoutException("Could not acquire lock to read balance");
        }
    }

    // Operation result
    public static class OperationResult implements Comparable<OperationResult> {
        public final int operationId;
        public final boolean success;

        public OperationResult(int operationId, boolean success) {
            this.operationId = operationId;
            this.success = success;
        }

        @Override
        public String toString() {
            return String.format("Op: %d has %s", operationId, success ? "succeeded" : "failed");
        }

        @Override
        public int compareTo(OperationResult o) {
            return Integer.compare(operationId, o.operationId);
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
                        account.deposit(amount, lockTimeoutMs) : account.withdraw(amount, lockTimeoutMs);
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
        System.out.println("FINAL_BALANCE " + account.getBalance(lockTimeoutMs));

        results.sort(Comparator.naturalOrder());
        results.forEach(System.out::println);
    }

        /*
     Extend the program so that, in addition to printing the final balance,
     it also prints a deterministic log of all operations,
     showing whether each operation succeeded or failed.

        Each operation must be logged exactly once, and the output must be ordered by operationId,
         regardless of the order in which the tasks actually execute.

        Constraints:

        The solution must not rely on execution order or thread scheduling.
        The output must always be deterministic for the same input.
        All shared data structures used for logging must be thread-safe.
     */

}
