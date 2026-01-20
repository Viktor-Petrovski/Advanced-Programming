import java.util.*;
import java.util.stream.IntStream;

public class StadiumTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }

    static class SeatTakenException extends Exception {
        public SeatTakenException() {
            super();
        }
    }

    static class SeatNotAllowedException extends Exception {
        public SeatNotAllowedException() {
            super();
        }
    }

    static class Sector implements Comparable<Sector> {
        private final String name;
        private final boolean[] seats; // true if taken --- 1 indexed
        private int type = 5; // random value

        Sector(String name, int size) {
            this.name = name;
            seats = new boolean[size];
        }

        void buyTicket(int seat, int type) throws SeatTakenException, SeatNotAllowedException {
            if (seats[seat - 1])
                throw new SeatTakenException();
            if (this.type == 5 && type != 0)
                this.type = type;
            else if ((this.type == 1 && type == 2) || (this.type == 2 && type == 1))
                throw new SeatNotAllowedException();

            seats[seat - 1] = true; // ticket bought
        }

        int amountFreeSeats() {
            int c = 0; // стримој не работат на булеан низи !?
            for (boolean seat : seats) {
                if (!seat)
                    c++;
            }
            return c;
        }

        @Override
        public String toString() {
            float percent = 100 - (amountFreeSeats() * 1.0f / seats.length) * 100.0f;
            return String.format("%s\t%d/%d\t%.1f%%", name, amountFreeSeats(), seats.length, percent);
            // H	88/100	12.0%
        }

        @Override
        public int compareTo(Sector o) {
            int i = Integer.compare(o.amountFreeSeats(), this.amountFreeSeats());
            if (i != 0) return i;
            return name.compareTo(o.name);
        }
    }

    static class Stadium {
        private final Map<String, Sector> sectorsMap;

        Stadium(String name) {
            sectorsMap = new HashMap<>();
        }

        void createSectors(String[] sectorNames, int[] sizes) {
            IntStream.range(0, sizes.length).forEach(i ->
                    sectorsMap.put(sectorNames[i], new Sector(sectorNames[i], sizes[i]))
            );
        }

        void buyTicket(String sectorName, int seat, int type) throws SeatTakenException, SeatNotAllowedException {
            sectorsMap.get(sectorName).buyTicket(seat, type);
        }

        void showSectors() {
            sectorsMap.values().stream().sorted().forEach(System.out::println);
        }
    }
}
