import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.IntStream;

public class F1Test {

    public static void main(String[] args) {
        F1Race f1Race = new F1Race();
        f1Race.readResults();
        f1Race.printSorted();
    }

    static class Driver implements Comparable<Driver> {
        private final String name;
        private final List<LocalTime> top3laps;

        Driver(String s) {
            String[] tokens = s.split("\\s+");
            this.name = tokens[0];

            DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                    .appendPattern("m:ss:SSS")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .toFormatter();

            this.top3laps = new ArrayList<>();
            top3laps.add(LocalTime.parse(tokens[1], fmt));
            top3laps.add(LocalTime.parse(tokens[2], fmt));
            top3laps.add(LocalTime.parse(tokens[3], fmt));

            top3laps.sort(Comparator.naturalOrder());
        }

        public LocalTime bestLap() {
            return top3laps.get(0);
        }

        @Override
        public String toString() {
            return String.format("%-10s%10s", name, bestLap().format(DateTimeFormatter.ofPattern("m:ss:SSS")));
        }

        @Override
        public int compareTo(Driver o) {
            return bestLap().compareTo(o.bestLap());
        }

    }

    static class F1Race {
        private final List<Driver> drivers;

        F1Race() {
            drivers = new ArrayList<>();
        }

        void readResults() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().forEach(l -> drivers.add(new Driver(l)));
        }

        void printSorted() {
            drivers.sort(Comparator.naturalOrder());

            IntStream.range(1, drivers.size() + 1)
                    .forEach(i -> System.out.printf("%d. %s\n", i, drivers.get(i - 1)));
        }
    }
}

