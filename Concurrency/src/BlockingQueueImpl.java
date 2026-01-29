import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueImpl<E> {
    private final Queue<E> queue;
    private final int maxCapacity;

    private final ReentrantLock lock;

    private final Condition notFull;
    private final Condition notEmpty;

    public BlockingQueueImpl(int maxCapacity) {
        queue = new LinkedList<>();
        this.maxCapacity = maxCapacity;

        lock = new ReentrantLock(true);
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    public void offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (queue.size() == maxCapacity) {
                if (nanos <= 0) return;
                nanos = notFull.awaitNanos(nanos);
            }

            queue.offer(e);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                if (nanos <= 0L) return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            E item = queue.poll();
            notFull.signalAll();
            return item;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        lock.lock();
        try {
            Iterator<E> iterator = queue.iterator();
            List<String> list = new ArrayList<>();
            while (iterator.hasNext())
                list.add(iterator.next().toString());
            return String.join(" -> ", list);
        } finally {
            lock.unlock();
        }
    }
}