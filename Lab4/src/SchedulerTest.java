import java.util.*;

public class SchedulerTest {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT")); // додадив

        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) {
            Scheduler<String> scheduler = new Scheduler<>();
            Date now = new Date();
            scheduler.add(new Date(now.getTime() - 7200000), jin.next());
            scheduler.add(new Date(now.getTime() - 3600000), jin.next());
            scheduler.add(new Date(now.getTime() - 14400000), jin.next());
            scheduler.add(new Date(now.getTime() + 7200000), jin.next());
            scheduler.add(new Date(now.getTime() + 14400000), jin.next());
            scheduler.add(new Date(now.getTime() + 3600000), jin.next());
            scheduler.add(new Date(now.getTime() + 18000000), jin.next());
            System.out.println(scheduler.getFirst());
            System.out.println(scheduler.getLast());
        }
        if (k == 3) { //test Scheduler with String
            Scheduler<String> scheduler = new Scheduler<>();
            Date now = new Date();
            scheduler.add(new Date(now.getTime() - 7200000), jin.next());
            scheduler.add(new Date(now.getTime() - 3600000), jin.next());
            scheduler.add(new Date(now.getTime() - 14400000), jin.next());
            scheduler.add(new Date(now.getTime() + 7200000), jin.next());
            scheduler.add(new Date(now.getTime() + 14400000), jin.next());
            scheduler.add(new Date(now.getTime() + 3600000), jin.next());
            scheduler.add(new Date(now.getTime() + 18000000), jin.next());
            System.out.println(scheduler.next());
            System.out.println(scheduler.last());
            ArrayList<String> res = scheduler.getAll(new Date(now.getTime() - 10000000), new Date(now.getTime() + 17000000));
            Collections.sort(res);
            for (String t : res) {
                System.out.print(t + " , ");
            }
        }
        if (k == 4) {//test Scheduler with ints complex
            Scheduler<Integer> scheduler = new Scheduler<>();
            int counter = 0;
            ArrayList<Date> to_remove = new ArrayList<>();

            while (jin.hasNextLong()) {
                Date d = new Date(jin.nextLong());
                int i = jin.nextInt();
                if ((counter & 7) == 0) {
                    to_remove.add(d);
                }
                scheduler.add(d, i);
                ++counter;
            }
            jin.next();

            while (jin.hasNextLong()) {
                Date l = new Date(jin.nextLong());
                Date h = new Date(jin.nextLong());
                ArrayList<Integer> res = scheduler.getAll(l, h);
                Collections.sort(res);
                System.out.println(l + " <: " + print(res) + " >: " + h);
            }
            System.out.println("test");
            ArrayList<Integer> res = scheduler.getAll(new Date(0), new Date(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
            for (Date d : to_remove) {
                scheduler.remove(d);
            }
            res = scheduler.getAll(new Date(0), new Date(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
        }
    }

    private static <T> String print(ArrayList<T> res) {
        if (res == null || res.isEmpty()) return "NONE";
        StringBuilder sb = new StringBuilder();
        for (T t : res) {
            sb.append(t).append(" , ");
        }
        return sb.substring(0, sb.length() - 3);
    }

    static class Scheduler<T> {
        private final TreeMap<Date, T> genericMap;

        Scheduler() {
            genericMap = new TreeMap<>();
        }

        void add(Date d, T t) {
            genericMap.put(d, t);
        }

        boolean remove(Date d) {
            return genericMap.remove(d) != null;
        }

        T next() {
            Date now = new Date();
            Map.Entry<Date, T> entry = genericMap.higherEntry(now); // враќа запис стриктно поголем од now
            return (entry != null) ? entry.getValue() : null;
        }

        T last() {
            Date now = new Date();
            Map.Entry<Date, T> entry = genericMap.lowerEntry(now);
            return entry != null ? entry.getValue() : null;
        }

        ArrayList<T> getAll(Date begin, Date end) {
            return new ArrayList<>(genericMap.subMap(begin, true, end, true).values());
        }

        T getFirst() {
            return genericMap.isEmpty() ? null : genericMap.firstEntry().getValue();
        }

        T getLast() {
            return genericMap.isEmpty() ? null : genericMap.lastEntry().getValue();
        }
    }
}