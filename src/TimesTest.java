import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class TimesTest {

    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes();
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(TimeFormat.FORMAT_AMPM);
    }

    enum TimeFormat {
        FORMAT_24, FORMAT_AMPM
    }

    static class UnsupportedFormatException extends Exception {
        public UnsupportedFormatException(String message) {
            super(message);
        }
    }

    static class InvalidTimeException extends Exception {
        public InvalidTimeException(String message) {
            super(message);
        }
    }

    static class Time {
        final int hour;
        final int minute;

        Time(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        @Override
        public String toString() {
            return String.format("%2d:%2d", hour, minute);
        }

        public String ampm() {
            String ts = hour >= 12 ? "PM" : "AM";
            int h = hour;
            if (hour == 0)
                h = 12;
            if (hour > 12)
                h %= 12;
            return String.format("%2d:%2d %s", h, minute, ts);

        }
    }

    static class TimeTable {
        private final List<Time> timeList = new ArrayList<>();

        void readTimes() throws UnsupportedFormatException, InvalidTimeException {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            Scanner sc = new Scanner(System.in);
            while (sc.hasNext()) {
                String l = sc.next();
                if (!l.contains(".") && !l.contains(":"))
                    throw new UnsupportedFormatException(l);

                String[] tokens = l.contains(".") ? l.split("\\.") : l.split(":");
                int hour = Integer.parseInt(tokens[0]);
                int minute = Integer.parseInt(tokens[1]);

                if (hour < 0 || hour > 23 || minute > 59 || minute < 0)
                    throw new InvalidTimeException(l);
                timeList.add(new Time(hour, minute));
            }
        }

        void writeTimes(TimeFormat format) {
            PrintWriter pw = new PrintWriter(System.out);
            if (format.equals(TimeFormat.FORMAT_24))
                timeList.stream().sorted(Comparator.comparing(Time::toString)).forEach(pw::println);
            else
                timeList.stream().sorted(Comparator.comparing(Time::toString)).map(Time::ampm).forEach(pw::println);
            pw.flush();

            // за првиот час од денот (0:00 - 0:59), додадете 12 и направете го "AM"
            //од 1:00 до 11:59, само направето го "AM"
            //од 12:00 до 12:59, само направето го "PM"
            //од 13:00 до 23:59 одземете 12 и направете го "PM"
        }
    }
}

