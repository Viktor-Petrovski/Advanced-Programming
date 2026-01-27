import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


public class StatisticsExam {

    public static void main(String[] args) {

        StatisticsService service = new StatisticsService();

        int k;
        Scanner scanner = new Scanner(System.in);
        k = scanner.nextInt();


        List<Callable<String>> tasks = new ArrayList<>();

        /* ------------------------------------------------------------
           PHASE 1: Concurrent writers
           ------------------------------------------------------------ */

        int added = 0;
        int avg = 0;
        int min = 0;
        int max = 0;

        int expectedMin = 10;
        int expectedMax = 10;

        for (int i = 1; i < k * 100; i++) {
            int value = i * 10;
            tasks.add(new SubmitNumberTask(service, value));
            expectedMax = Math.max(expectedMax, value);
            added++;
        }

        /* ------------------------------------------------------------
           PHASE 2: Concurrent readers (should run in parallel)
           ------------------------------------------------------------ */

        for (int i = 0; i < k * 5; i++) {
            tasks.add(new GetAverageTask(service));
            avg++;
            tasks.add(new GetMinTask(service));
            min++;
            tasks.add(new GetMaxTask(service));
            max++;
        }

        /* ------------------------------------------------------------
           PHASE 3: Interleaved read/write (critical part)
           ------------------------------------------------------------ */

        for (int i = 100; i <= k * 200; i += 10) {
            tasks.add(new SubmitNumberTask(service, i));
            added++;
            expectedMax = Math.max(expectedMax, i);
            tasks.add(new GetAverageTask(service));
            avg++;
            tasks.add(new GetMinTask(service));
            min++;
            tasks.add(new GetMaxTask(service));
            max++;
        }

        /* ------------------------------------------------------------
           EXECUTION
           ------------------------------------------------------------ */


        List<Future<String>> results = ConcurrentService.submitAll(6, tasks);

        List<String> finalResults = new ArrayList<>();
        for (Future<String> f : results) {
            try {
                finalResults.add(f.get());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        int numberAddedMessage = 0, minInvoked = 0, maxInvoked = 0, averageInvoked = 0;

        for (String finalResult : finalResults) {
            if (finalResult.startsWith("AVERAGE")) {
                averageInvoked++;
            }
            if (finalResult.startsWith("MIN")) {
                minInvoked++;
            }
            if (finalResult.startsWith("MAX")) {
                maxInvoked++;
            }
            if (finalResult.contains("Total numbers: ")) {
                numberAddedMessage++;
            }
        }

        if (minInvoked != min) {
            System.out.println("GetMinTask was not invoked the correct number of times");
        }

        if (maxInvoked != max) {
            System.out.println("GetMaxTask was not invoked the correct number of times");
        }

        if (averageInvoked != avg) {
            System.out.println("GetAverageTask was not invoked the correct number of times");
        }

        if (numberAddedMessage != added) {
            System.out.println("Number of added tasks was not invoked the correct number of times");
        }

        /* ------------------------------------------------------------
           BASIC SANITY CHECKS (NO assert, exam-safe)
           ------------------------------------------------------------ */

        int finalCount = service.getCount();


        if (finalCount != added) {
            throw new RuntimeException(
                    String.format("ERROR: Expected %d numbers, but got %d", added, finalCount)
            );
        }

        if (service.getMin() != expectedMin) {
            throw new RuntimeException(
                    "ERROR: Expected MIN = " + expectedMin
            );
        }

        if (service.getMax() != expectedMax) {
            throw new RuntimeException(
                    "ERROR: Expected MAX = " + expectedMax
            );
        }

        System.out.println("✔ FINAL CHECKS PASSED");
    }

    /// Да се имплементира класа StatisticsService која ќе управува со цели броеви и ќе нуди методи за
    /// додавање на нов цел број во колекцијата, како и методи за
    /// добивање на бројот, минимумот, максимумот и просечната вредност на броевите во колекцијата.
    /// Од оваа класа е креиран само еден објект во main функцијата и истиот тој објект се користи од сите задачи
    static class StatisticsService {
        public final ReadWriteLock lock;
        public final List<Integer> nums;

        StatisticsService() {
            lock = new ReentrantReadWriteLock();
            nums = new ArrayList<>();
        }

        int getCount() {
            lock.readLock().lock();
            try {
                return nums.size();
            } finally {
                lock.readLock().unlock();
            }
        }

        int getMin() {
            lock.readLock().lock();
            try {
                return nums.stream().mapToInt(i -> i).min().orElse(0);
            } finally {
                lock.readLock().unlock();
            }
        }

        int getMax() {
            lock.readLock().lock();
            try {
                return nums.stream().mapToInt(i -> i).max().orElse(0);
            } finally {
                lock.readLock().unlock();
            }
        }

        double getAvg() {
            lock.readLock().lock();
            try {
                return nums.stream().mapToInt(i -> i).average().orElse(.0);
            } finally {
                lock.readLock().unlock();
            }
        }

        void addNumber(int num) {
            lock.writeLock().lock();
            try {
                nums.add(num);
            } finally {
                lock.writeLock().unlock();
            }
        }

    }


    /// задача која ќе додаде еден нов број во сервисот (StatisticsService),
    /// а како резултат ќе врати стринг со опис на извршената операција
    /// (NUMBER %d ADDED. Total numbers: %d)
    static class SubmitNumberTask implements Callable<String> {
        private final StatisticsService service;
        private final int value;

        SubmitNumberTask(StatisticsService service, int value) {
            this.service = service;
            this.value = value;
        }

        @Override
        public String call() {
            service.addNumber(value);
            int totalNumbers = service.getCount();
            return String.format("NUMBER %d ADDED. Total numbers: %d", value, totalNumbers);
        }
    }


    /// задача која ќе го извлече просекот на додадените броеви од сервисот,
    ///  а како резултат ќе врати стринг со опис на извршената операција
    /// (AVERAGE: %.2f)
    static class GetAverageTask implements Callable<String> {
        private final StatisticsService service;

        GetAverageTask(StatisticsService service) {
            this.service = service;
        }

        @Override
        public String call() {
            return String.format("AVERAGE: %.2f", service.getAvg());
        }
    }

    /// задача која ќе го извлече минимумот на додадените броеви од сервисот,
    /// а како резултат ќе врати стринг со опис на извршената операција
    /// (MIN: %.2f)
    static class GetMinTask implements Callable<String> {
        private final StatisticsService service;

        GetMinTask(StatisticsService service) {
            this.service = service;
        }

        @Override
        public String call() {
            return String.format("MIN: %.2f", (double) service.getMin());
        }

    }

    /// задача која ќе го извлече максимумот на додадените броеви од сервисот,
    /// а како резултат ќе врати стринг со опис на извршената операција
    /// (MAX: %.2f)
    static class GetMaxTask implements Callable<String> {
        private final StatisticsService service;

        GetMaxTask(StatisticsService service) {
            this.service = service;
        }

        @Override
        public String call() {
            return String.format("MAX: %.2f", (double) service.getMax());
        }
    }


    /// Да се имплементира класа ConcurrentService со единствен статички метод submitAll кој прима два аргументи:
    /// број на нишки кои може да се користат и листа на Callable<String> објекти.
    /// Методот треба да ги изврши сите callable задачи на ExecutorService со определениот број на нишки,
    /// а да врати листа од сите идни резултати
    static class ConcurrentService {
        static List<Future<String>> submitAll(int amountThreads, List<Callable<String>> tasks) {
            ExecutorService exe = Executors.newFixedThreadPool(amountThreads);
            List<Future<String>> res = tasks.stream()
                    .map(exe::submit)
                    .collect(Collectors.toCollection(ArrayList::new));
            exe.shutdown();
            return res;
        }
    }
}
