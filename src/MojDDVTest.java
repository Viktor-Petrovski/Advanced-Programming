import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MojDDVTest {

    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords();

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns();

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics();


    }

    static class AmountNotAllowedException extends Exception {
        public AmountNotAllowedException(int sum) {
            super(String.format("Receipt with amount %d is not allowed to be scanned", sum));
        }
    }

    static class Receipt {
        private final static int LIMIT = 30_000;
        private final static double TAX_RETURN = .15;

        private final static double A = 0.18;
        private final static double B = 0.05;
        private final static double V = 0.0;

        private final static double EPSILON = 1e-10;

        private final String id;
        private int sum;
        double taxReturn;

        Receipt(String line) throws AmountNotAllowedException {
            String[] tokens = line.split("\\s+");
            id = tokens[0];
            sum = 0;
            taxReturn = 0;

            for (int i = 1; i < tokens.length; i++) {
                int amt = Integer.parseInt(tokens[i++]);
                char type = tokens[i].charAt(0);
                sum += amt;

                double currentTax = 0;
                if (type == 'A') currentTax = amt * A * TAX_RETURN;
                else if (type == 'B') currentTax = amt * B * TAX_RETURN;
                else if (type == 'V') currentTax = amt * V * TAX_RETURN;

                taxReturn += currentTax;
            }

            if (sum > LIMIT)
                throw new AmountNotAllowedException(sum);

            taxReturn += EPSILON;
            taxReturn = Math.abs(taxReturn);

            // ID item_price1 item_tax_type1 item_price2 item_tax_type2 … item_price-n item_tax_type-n
        }

        @Override
        public String toString() {
            return String.format("%10s\t%10d\t%10.5f", id, sum, taxReturn);
        }
    }

    static class MojDDV {
        private final List<Receipt> receiptList;

        MojDDV() {
            receiptList = new ArrayList<>();
        }

        void readRecords() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().forEach(l -> {
                try {
                    Receipt ins = new Receipt(l);
                    receiptList.add(ins);
                } catch (AmountNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            });
        }


        void printTaxReturns() {
            PrintWriter pw = new PrintWriter(System.out);
            receiptList.forEach(pw::println);
            pw.flush();
        }

        void printStatistics() {
            System.out.printf("min:\t%5.3f\n", (float) receiptList.stream().mapToDouble(r -> r.taxReturn).min().orElse(.0));
            System.out.printf("max:\t%5.3f\n", (float) receiptList.stream().mapToDouble(r -> r.taxReturn).max().orElse(.0));
            System.out.printf("sum:\t%5.3f\n", (float) receiptList.stream().mapToDouble(r -> r.taxReturn).sum());
            System.out.printf("count:\t%2d\n", (long) receiptList.size());
            System.out.printf("avg:\t%5.3f\n", (float) receiptList.stream().mapToDouble(r -> r.taxReturn).average().orElse(.0));
        }
    }
}