import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.DoubleStream;

public class DailyTemperatureTest {
    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures();
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats('C');
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats('F');
    }

    static class DailyTemperatures {
        private final Map<Integer, List<Double>> dayMap; // 123 -> list<measurements>

        DailyTemperatures() {
            dayMap = new TreeMap<>();
        }

        private void readDay(String s) { // standardize in Celsius

            Function<String, Double> parser = e -> Double.parseDouble(e.substring(0, e.length() - 1));
            Function<String, Double> fn = s.contains("F") ? t -> (parser.apply(t) - 32) / 1.8 : parser;

            String[] tokens = s.split("\\s+");
            Integer day = Integer.parseInt(tokens[0].trim());
            List<Double> measurements = new ArrayList<>();

            Arrays.stream(tokens).skip(1)
                    .mapToDouble(fn::apply)
                    .forEach(measurements::add);

            dayMap.put(day, measurements);
        }

        void readTemperatures() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().forEach(this::readDay);
        }

        private DoubleStream ds(List<Double> list, char unit) {
            return list.stream().mapToDouble(i -> unit == 'F' ? i * 1.8 + 32 : i);
        }

        void writeDailyStats(char unit) {
            dayMap.forEach((k, v) -> {
                double min = ds(v, unit).min().orElse(.0);
                double max = ds(v, unit).max().orElse(.0);
                double avg = ds(v, unit).average().orElse(.0);

                System.out.printf("%3s: Count: %3d Min: %6.2f%c Max: %6.2f%c Avg: %6.2f%c\n",
                        k, v.size(), min, unit, max, unit, avg, unit);
            });
        }
    }
}
