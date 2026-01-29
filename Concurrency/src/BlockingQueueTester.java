import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class BlockingQueueTester {
    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        BlockingQueueImpl<Integer> blockingQueue = new BlockingQueueImpl<>(20);
        long timeoutMillis = 300L;

        final Runnable producer = () ->
                IntStream.range(0, 50).forEach(i -> {
                    try {
                        int ins = random.nextInt(500);

                        blockingQueue.offer(ins, timeoutMillis, TimeUnit.MILLISECONDS);
                        System.out.println("Added: " + ins);

                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        final Runnable consumer = () ->
                IntStream.range(0, 50).forEach(i -> {
                    try {
                        int rm = blockingQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
                        System.out.println("Removed: " + rm);

                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        List<Thread> threads = new ArrayList<>();

        threads.add(new Thread(producer, "Producer-1"));
        threads.add(new Thread(producer, "Producer-2"));
        threads.add(new Thread(consumer, "Consumer-1"));

        threads.forEach(Thread::start);

        for (Thread thread : threads)
            thread.join();

        System.out.println("\nCurrent queue status:");
        System.out.println(blockingQueue);
    }
}
