import java.util.*;

public class TripleTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
        int c = sc.nextInt();
        Triple<Integer> tInt = new Triple<>(a, b, c);
        System.out.printf("%.2f\n", tInt.max());
        System.out.printf("%.2f\n", tInt.average());
        tInt.sort();
        System.out.println(tInt);
        float fa = sc.nextFloat();
        float fb = sc.nextFloat();
        float fc = sc.nextFloat();
        Triple<Float> tFloat = new Triple<>(fa, fb, fc);
        System.out.printf("%.2f\n", tFloat.max());
        System.out.printf("%.2f\n", tFloat.average());
        tFloat.sort();
        System.out.println(tFloat);
        double da = sc.nextDouble();
        double db = sc.nextDouble();
        double dc = sc.nextDouble();
        Triple<Double> tDouble = new Triple<>(da, db, dc);
        System.out.printf("%.2f\n", tDouble.max());
        System.out.printf("%.2f\n", tDouble.average());
        tDouble.sort();
        System.out.println(tDouble);
    }

    static class Triple<T extends Number> {
        private final List<T> nums;

        Triple(T a, T b, T c) {
            nums = new ArrayList<>();
            nums.add(a);
            nums.add(b);
            nums.add(c);
        }

        double max() {
            return nums.stream().mapToDouble(Number::doubleValue).max().orElse(.0);
        }

        double average() {
            return nums.stream().mapToDouble(Number::doubleValue).average().orElse(.0);
        }

        void sort() {
            nums.sort(Comparator.comparing(Number::doubleValue));
        }

        @Override
        public String toString() {
            return String.format("%.2f %.2f %.2f",
                    nums.get(0).doubleValue(),
                    nums.get(1).doubleValue(),
                    nums.get(2).doubleValue());
        }
    }
}
