import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class ParkingTesting {
    static class DateUtil {
        /// кој пресметува изминати минути помеѓу два објекти од класата LocalDateTime
        public static long durationBetween(LocalDateTime start, LocalDateTime end) {
            return Duration.between(start, end).toMinutes();
        }
    }

    public static <K, V extends Comparable<V>> void printMapSortedByValue(Map<K, V> map) {
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> System.out.printf("%s -> %s%n", entry.getKey().toString(), entry.getValue().toString()));

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int capacity = Integer.parseInt(sc.nextLine());

        Parking parking = new Parking(capacity);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equals("update")) {
                String registration = parts[1];
                String spot = parts[2];
                LocalDateTime timestamp = LocalDateTime.parse(parts[3]);
                boolean entrance = Boolean.parseBoolean(parts[4]);
                parking.update(registration, spot, timestamp, entrance);
            } else if (parts[0].equals("currentState")) {
                System.out.println("PARKING CURRENT STATE");
                parking.currentState();
            } else if (parts[0].equals("history")) {
                System.out.println("PARKING HISTORY");
                parking.history();
            } else if (parts[0].equals("carStatistics")) {
                System.out.println("CAR STATISTICS");
                printMapSortedByValue(parking.carStatistics());
            } else if (parts[0].equals("spotOccupancy")) {
                LocalDateTime start = LocalDateTime.parse(parts[1]);
                LocalDateTime end = LocalDateTime.parse(parts[2]);
                printMapSortedByValue(parking.spotOccupancy(start, end));
            }
        }
    }

    static class Reservation {
        private final String registration;
        private final String spot;
        private final LocalDateTime startTimestamp;
        private LocalDateTime endTimestamp;

        Reservation(String registration, String spot, LocalDateTime startTimestamp) {
            this.registration = registration;
            this.spot = spot;
            this.startTimestamp = startTimestamp;
            this.endTimestamp = null;
        }

        public String getRegistration() {
            return registration;
        }

        public String getSpot() {
            return spot;
        }

        public LocalDateTime getStartTimestamp() {
            return startTimestamp;
        }

        public LocalDateTime getEndTimestamp() {
            return endTimestamp;
        }

        public void setEndTimestamp(LocalDateTime outTimestamp) {
            this.endTimestamp = outTimestamp;
        }

        long durationBetween() {
            return DateUtil.durationBetween(startTimestamp, endTimestamp);
        }

        @Override
        public String toString() {
            String ending = endTimestamp == null ? "" : String.format("End timestamp: %s Duration in minutes: %d",
                    endTimestamp, durationBetween());

            return String.format("Registration number: %s Spot: %s Start timestamp: %s %s",
                    registration, spot, startTimestamp, ending);
        }
    }

    static class Parking {
        private final int capacity;
        private final Map<String, Reservation> currentlyParked; // registration -> Res
        private final Map<String, Reservation> noLongerParked; // history
        private final Map<String, Integer> amountTimesParked; // registration -> times parked
        private final Map<String, Set<Reservation>> spotMap; // history
        private int occupiedAmount;

        Parking(int capacity) {
            this.capacity = capacity;
            occupiedAmount = 0;

            currentlyParked = new HashMap<>();
            noLongerParked = new HashMap<>();
            amountTimesParked = new TreeMap<>();
            spotMap = new HashMap<>();
        }

        /// метод за ажурирање на информации за едно возило во паркингот.
        /// Овој метод секогаш ќе се повикува точно 2 пати, прв пат за влез на возилото во паркингот и втор пат за излез од паркингот.
        /// Доколку entry e true, тоа означува возилото со регистрација registration се паркира на местото spot во времето timestamp.
        /// Доколку entry е false, тоа означува дека возилото излегува од паркингот во времето timestamp.
        void update(String registration, String spot, LocalDateTime timestamp, boolean entry) {
            Reservation reservation = entry ? new Reservation(registration, spot, timestamp) : currentlyParked.get(registration);
            if (occupiedAmount >= capacity || reservation == null)
                return;

            if (entry) {
                currentlyParked.put(registration, reservation);
                amountTimesParked.putIfAbsent(registration, 0);
                amountTimesParked.put(registration, amountTimesParked.get(registration) + 1);
            } else {
                reservation.setEndTimestamp(timestamp);
                noLongerParked.put(registration, reservation);
                currentlyParked.remove(registration);

                spotMap.computeIfAbsent(spot, k -> new HashSet<>()).add(reservation);
            }

            occupiedAmount = entry ? occupiedAmount + 1 : occupiedAmount - 1;
        }

        /// метод кој печати колку % е исполнет капацитетот на паркингот во моментот и потоа ги печати информациите за
        /// моментално паркираните возила сортирани според времето на влез во паркингот во опаѓачки редослед
        void currentState() {
            System.out.printf("Capacity filled: %.2f%%\n", (occupiedAmount / (1.0 * capacity) * 100));
            currentlyParked.values().stream()
                    .sorted(Comparator.comparing(Reservation::getStartTimestamp).reversed())
                    .forEach(System.out::println);
        }

        /// метод кој печати информации за паркирани возила кои веќе го напуштиле паркингот,
        /// сортирани според времетраењето на паркирањето во опаѓачки редослед.
        void history() {
            noLongerParked.values().stream()
                    .sorted(Comparator.comparing(Reservation::durationBetween).reversed())
                    .forEach(System.out::println);
        }

        /// метод кој враќа мапа во која клуч е регистрација на возило, а вредност е бројот на паркирања на тоа возило на паркингот.
        /// Паровите да се сортирани според клучот.
        /// За паркирање се смета и активно паркирање т.е. доколку возилото моментално e во паркингот
        Map<String, Integer> carStatistics() {
            return amountTimesParked;
        }

        private long calculateTime(LocalDateTime start, LocalDateTime end, Set<Reservation> reservations) {
            return reservations.stream().mapToLong(r -> {
                LocalDateTime from = r.getStartTimestamp().isAfter(start) ? r.getStartTimestamp() : start;
                LocalDateTime to = r.getEndTimestamp().isBefore(end) ? r.getEndTimestamp() : end;
                return DateUtil.durationBetween(from, to);
            }).sum();
        }

        /// метод кој враќа мапа во која клуч е ID на паркинг местото,
        /// а вредноста е процентот од времето изминато од start до end во кое паркинг местото било зафатено
        Map<String, Double> spotOccupancy(LocalDateTime start, LocalDateTime end) {
            Map<String, Set<Reservation>> copySpotMap = new HashMap<>(spotMap);
            currentlyParked.values().forEach(r -> copySpotMap.get(r.getSpot()).add(r));

            long totalTime = DateUtil.durationBetween(start, end);
            return copySpotMap.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> {
                        double res = (calculateTime(start, end, e.getValue())) / (totalTime * 1.0) * 100;
                        return res > 0 && res < 100 ? res : 0.0;
                    }
            ));
        }
    }
}
