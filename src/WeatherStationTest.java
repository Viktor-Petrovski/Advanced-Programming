import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurement(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }

    static class Measurement implements Comparable<Measurement> {
        final float temperature;
        final float wind;
        final float humidity;
        final float visibility;
        final Date date;

        Measurement(float temperature, float wind, float humidity, float visibility, Date date) {
            this.temperature = temperature;
            this.wind = wind;
            this.humidity = humidity;
            this.visibility = visibility;
            this.date = date;
        }

        @Override
        public int compareTo(Measurement o) {
            return this.date.compareTo(o.date);
        }

        @Override
        public String toString() {
            // 24.6 80.2 km/h 28.7% 51.7 km Tue Dec 17 23:40:15 CET 2013
            return String.format("%.1f %.1f km/h %.1f%% %.1f km %s",
                    temperature, wind, humidity, visibility, date.toString().replace("UTC", "GMT"));
        }
    }

    static class WeatherStation {
        private final int days;
        private final TreeSet<Measurement> measurementSet;

        WeatherStation(int days) {
            this.days = days;
            measurementSet = new TreeSet<>();
        }

        public void addMeasurement(float temperature, float wind, float humidity, float visibility, Date date) {
            Measurement ins = new Measurement(temperature, wind, humidity, visibility, date);
            if (measurementSet.isEmpty()) {
                measurementSet.add(ins);
                return;
            }

            Measurement lower = measurementSet.lower(ins);
            Measurement higher = measurementSet.higher(ins);

            if (lower != null && Math.abs(ins.date.getTime() - lower.date.getTime()) < 150000 ||
            higher != null && Math.abs(ins.date.getTime() - higher.date.getTime()) < 150000) return;

            measurementSet.add(ins);

            long limitInMillis = (long) days * 24 * 60 * 60 * 1000;
            measurementSet.removeIf(m -> (date.getTime() - m.date.getTime()) >= limitInMillis);

        }

        public int total() {
            return measurementSet.size();
        }

        // ги печати сите мерења во периодот од from до to подредени според датумот во растечки редослед и на
        // крај ја печати просечната температура во овој период. Ако не постојат мерења во овој период се фрла
        // исклучок од тип RuntimeException (вграден во Јава).
        public void status(Date from, Date to) {
            TreeSet<Measurement> res = measurementSet.stream()
                    .filter(m -> (m.date.after(from) && m.date.before(to)) ||
                            m.date.getTime() == from.getTime() || m.date.getTime() == to.getTime())
                    .collect(Collectors.toCollection(TreeSet::new));

            if (res.isEmpty())
                throw new RuntimeException();

            res.forEach(System.out::println);
            System.out.printf("Average temperature: %.2f",
                    res.stream().mapToDouble(m -> m.temperature).average().orElse(.0));
        }
    }
}

