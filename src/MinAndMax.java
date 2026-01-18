import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

public class MinAndMax {
    static class MinMax <T extends Comparable<T>>{
        private T min;
        private T max;
        private final List<T> history;

        MinMax() {
            history = new ArrayList<>();
        }

        void update(T element) {
            if (history.isEmpty()){
                min = element;
                max = element;
            }
            else {
                if (element.compareTo(min) < 0)
                    min = element;
                if (element.compareTo(max) > 0)
                    max = element;
            }

            history.add(element);
        }

        long getAmount() {
            Predicate<T> cond = t -> !t.equals(min) && !t.equals(max);
            return history.stream().filter(cond).count();
        }

        @Override
        public String toString() {
            return String.format("%s %s %d\n", min, max, getAmount());
        }
    }
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for(int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for(int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}