public class PricingEngine {

    public static void main(String[] args) {

        PriceCalculator calculator =
                new TaxDecorator(
                        new PercentageDiscountDecorator(
                                new FixedDiscountDecorator(
                                        new BasePriceCalculator(100.0), // base price
                                        10.0                              // $10 coupon
                                ),
                                0.20                                  // 20% discount
                        ),
                        0.18                                      // 18% VAT
                );

        System.out.println("Final price: " + calculator.calculate());

        System.out.println("\n---- Another scenario ----");

        PriceCalculator noDiscount =
                new TaxDecorator(
                        new BasePriceCalculator(50.0),
                        0.10
                );

        System.out.println("Final price: " + noDiscount.calculate());
    }

    interface PriceCalculator {
        double calculate();
    }

    static class BasePriceCalculator implements PriceCalculator {
        private final double basePrice;

        BasePriceCalculator(double basePrice) {
            this.basePrice = basePrice;
        }

        @Override
        public double calculate() {
            return basePrice;
        }
    }

    static abstract class PriceDecorator implements PriceCalculator {
        protected final PriceCalculator calculator;

        PriceDecorator(PriceCalculator calculator) {
            this.calculator = calculator;
        }
    }

    static class TaxDecorator extends PriceDecorator {
        private final double vat;

        TaxDecorator(PriceCalculator calculator, double vat) {
            super(calculator);
            this.vat = vat;
        }

        @Override
        public double calculate() {
            double price = calculator.calculate();
            return price + (price * vat);
        }
    }

    static class PercentageDiscountDecorator extends PriceDecorator {
        private final double percent;

        PercentageDiscountDecorator(PriceCalculator calculator, double percent) {
            super(calculator);
            this.percent = percent;
        }

        @Override
        public double calculate() {
            double price = calculator.calculate();
            return price - (price * percent);
        }

    }

    static class FixedDiscountDecorator extends PriceDecorator {
        private final double coupon;

        FixedDiscountDecorator(PriceCalculator calculator, double coupon) {
            super(calculator);
            this.coupon = coupon;
        }

        @Override
        public double calculate() {
            double res = calculator.calculate() - coupon;
            return Math.max(res, 0);
        }
    }
}