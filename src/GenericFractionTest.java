import java.util.Scanner;

public class GenericFractionTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double n1 = scanner.nextDouble();
        double d1 = scanner.nextDouble();
        float n2 = scanner.nextFloat();
        float d2 = scanner.nextFloat();
        int n3 = scanner.nextInt();
        int d3 = scanner.nextInt();
        try {
            GenericFraction<Double, Double> gfDouble = new GenericFraction<>(n1, d1);
            GenericFraction<Float, Float> gfFloat = new GenericFraction<>(n2, d2);
            GenericFraction<Integer, Integer> gfInt = new GenericFraction<>(n3, d3);
            System.out.printf("%.2f\n", gfDouble.toDouble());
            System.out.println(gfDouble.add(gfFloat));
            System.out.println(gfInt.add(gfFloat));
            System.out.println(gfDouble.add(gfInt));
            gfInt = new GenericFraction<>(n3, 0);
        } catch (ZeroDenominatorException e) {
            System.out.println(e.getMessage());
        }

        scanner.close();
    }

    static class ZeroDenominatorException extends Exception {
        public ZeroDenominatorException() {
            super("Denominator cannot be zero");
        }
    }

    static class GenericFraction<T extends Number, U extends Number> {
        private final T numerator;
        private final U denominator;

        /// конструктор кој ги иницијализира броителот и именителот на дропката.
        /// Ако се обидиме да иницијализираме дропка со 0 вредност за именителот треба да се фрли исклучок
        /// од тип ZeroDenominatorException
        GenericFraction(T numerator, U denominator) throws ZeroDenominatorException {
            if (denominator.doubleValue() == .0)
                throw new ZeroDenominatorException();

            this.numerator = numerator;
            this.denominator = denominator;
        }

        GenericFraction<Double, Double> add(GenericFraction<? extends Number, ? extends Number> gf)
                throws ZeroDenominatorException {
            Double num = this.numerator.doubleValue() * gf.denominator.doubleValue() +
                    this.denominator.doubleValue() * gf.numerator.doubleValue();
            Double den = this.denominator.doubleValue() * gf.denominator.doubleValue();
            return new GenericFraction<>(num, den);
        }

        double toDouble() {
            return numerator.doubleValue() / denominator.doubleValue();
        }

        @Override
        public String toString() {
            // 19.00 / 35.00
            double num = numerator.doubleValue();
            double den = denominator.doubleValue();

            // кратење дропка ахх
            for (int i = 2; i < 10; i++) {
                while (num % i == 0 && den % i == 0) {
                    num /= i;
                    den /= i;
                }
            }

            return String.format("%.2f / %.2f", num, den);
        }

    }
}
