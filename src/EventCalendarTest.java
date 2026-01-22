import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

public class EventCalendarTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        EventCalendar eventCalendar = new EventCalendar(year);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            Date date = df.parse(parts[2]);
            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }
        Date date = df.parse(scanner.nextLine());
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }

    static class WrongDateException extends Exception {
        public WrongDateException(Date date) {
            super(String.format("Wrong date: %s", date.toString()));
        }
    }

    static class Event implements Comparable<Event> {
        private final String name;
        private final String location;
        private final Date date;

        Event(String name, String location, Date date) {
            this.name = name;
            this.location = location;
            this.date = date;
        }

        @Override
        public int compareTo(Event o) {
            int i = date.compareTo(o.date);
            if (i != 0) return i;
            i = name.compareTo(o.name);
            return (i != 0) ? i : location.compareTo(o.location);
        }

        @Override
        public String toString() {
            // dd MMM, YYY HH:mm at [location], [name]
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("dd MMM, yyy HH:mm");
            String d = sdf.format(date);
            return String.format("%s at %s, %s", d, location, name);
        }
    }

    static class EventCalendar {
        private final int year;
        private final Map<Integer, Map<Integer, Set<Event>>> monthMap; // month -> map<day, set<events>>

        public EventCalendar(int year) {
            this.year = year;
            monthMap = new TreeMap<>();
            populate();
        }

        void populate() {
            IntStream.range(1, 13).forEach(i -> monthMap.put(i, new TreeMap<>()));
        }

        public int getYear(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.YEAR);
        }

        public int getMonth(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.MONTH) + 1;
        }

        public int getDay(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.DAY_OF_MONTH);
        }

        public void addEvent(String name, String location, Date date) throws WrongDateException {
            int year = getYear(date);
            if (year != this.year)
                throw new WrongDateException(date);

            int month = getMonth(date);
            int day = getDay(date);

            Map<Integer, Set<Event>> dayMap = monthMap.get(month);
            dayMap.computeIfAbsent(day, k -> new TreeSet<>()).add(new Event(name, location, date));
        }

        public void listEvents(Date date) {
            int month = getMonth(date);
            int day = getDay(date);

            Set<Event> events = monthMap.get(month).get(day);

            if (events != null)
                events.forEach(System.out::println);
            else System.out.println("No events on this day!");
        }

        public void listByMonth() {
            monthMap.forEach((k, v) ->
                System.out.printf("%d : %d\n", k, v.values().stream().mapToInt(Collection::size).sum())
            );
        }
    }
}
