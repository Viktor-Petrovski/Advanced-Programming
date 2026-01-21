import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }

    static class Flight {
        private final String from;
        private final String to;
        private final LocalTime timeDeparture;
        private final LocalTime timeArrival;

        Flight(String from, String to, int timeDeparture, int duration) {
            this.from = from;
            this.to = to;
            this.timeDeparture = LocalTime.MIDNIGHT.plusMinutes(timeDeparture);
            timeArrival = this.timeDeparture.plusMinutes(duration);
        }

        public LocalTime timeDeparture() {
            return timeDeparture;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        private int getTime(LocalTime lt) {
            return lt.getHour() * 60 + lt.getMinute();
        }

        @Override
        public String toString() {
            //HND-PVG 21:13-01:29 +1d 4h16m
            //HND-PEK 04:59-06:49 1h50m

            int MINUTES_PER_DAY = 24 * 60;
            int totalTime = getTime(timeArrival) - getTime(timeDeparture);

            LocalTime res = LocalTime.MIDNIGHT.plusMinutes(totalTime % MINUTES_PER_DAY);

            String totalTravelDays = totalTime < 0 ? "+1d " : "";
            String totalHoursAndMinutes = String.format("%dh%02dm", res.getHour(), res.getMinute());

            return String.format("%s-%s %s-%s %s", from, to, timeDeparture, timeArrival, totalTravelDays + totalHoursAndMinutes);
        }

        public static final Comparator<Flight> BY_TO_THEN_TIME =
                Comparator.comparing(Flight::getTo).thenComparing(Flight::timeDeparture).thenComparing(Flight::getFrom);
    }

    static class Airport {
        private final String name;
        private final String country;
        private final String code;
        private final int passengers;

        Airport(String name, String country, String code, int passengers) {
            this.name = name;
            this.country = country;
            this.code = code;
            this.passengers = passengers;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Airport airport = (Airport) o;
            return Objects.equals(code, airport.code);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(code);
        }
    }

    static class Airports {
        private final Map<String, Airport> airportMap;
        private final Map<String, Set<Flight>> fromFlightMap; // from -> set<flights>
        private final Map<String, Set<Flight>> toFlightMap; // to -> set<flights>
        private final List<Flight> flights; // from -> set<flights>

        Airports() {
            airportMap = new HashMap<>();
            fromFlightMap = new HashMap<>();
            toFlightMap = new HashMap<>();
            flights = new ArrayList<>();
        }

        public void addAirport(String name, String country, String code, int passengers) {
            airportMap.put(code, new Airport(name, country, code, passengers));
        }

        public void addFlights(String from, String to, int time, int duration) {
            Set<Flight> fromSet = fromFlightMap.computeIfAbsent(from, k -> new TreeSet<>(Flight.BY_TO_THEN_TIME));
            Set<Flight> toSet = toFlightMap.computeIfAbsent(to, k -> new TreeSet<>(Flight.BY_TO_THEN_TIME));

            Flight ins = new Flight(from, to, time, duration);

            fromSet.add(ins);
            toSet.add(ins);
            flights.add(ins);
        }

        public void showFlightsFromAirport(String code) {
            List<Flight> fl = fromFlightMap.get(code).stream()
                    .filter(f -> f.getFrom().equals(code))
                    .distinct()
                    .collect(Collectors.toCollection(ArrayList::new));

            Airport airport = airportMap.get(code);
            //Tokyo International (HND)
            //Japan
            //66795178
            System.out.printf("%s (%s)\n%s\n%d\n", airport.name, airport.code, airport.country, airport.passengers);

            IntStream.range(1, fl.size() + 1)
                    .forEach(i -> System.out.printf("%d. %s\n", i, fl.get(i - 1)));

        }

        public void showDirectFlightsFromTo(String from, String to) {
            List<Flight> target = flights.stream()
                    .filter(f -> f.getFrom().equals(from))
                    .filter(f -> f.getTo().equals(to))
                    .collect(Collectors.toCollection(ArrayList::new));

            if (target.isEmpty()) {
                System.out.printf("No flights from %s to %s\n", from, to);
                return;
            }

            target.forEach(System.out::println);
        }

        public void showDirectFlightsTo(String code) {
            toFlightMap.get(code).stream()
                    .filter(f -> f.getTo().trim().equals(code.trim()))
                    .forEach(System.out::println);

        }
    }
}
