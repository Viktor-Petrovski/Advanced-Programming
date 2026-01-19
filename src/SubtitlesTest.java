import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SubtitlesTest {
    public static void main(String[] args) {
        Subtitles subtitles = new Subtitles();
        int n = subtitles.loadSubtitles();
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.printf("SHIFT FOR %d ms%n", shift);
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();

    }

    static class Part {
        private final String id;
        private LocalTime from;
        private LocalTime to;
        private final List<String> text;

        private Part(String id, LocalTime from, LocalTime to, List<String> text) {
            this.id = id;
            this.from = from;
            this.to = to;
            this.text = text;
        }

        private static LocalTime parser(String s) {
            // 00:00:48,321
            return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm:ss,SSS"));
        }

        private static String formatter(LocalTime lt) {
            return lt.format(DateTimeFormatter.ofPattern("HH:mm:ss,SSS"));
        }

        void shift(int ms) {
            long NS = 1_000_000L;
            from = from.plusNanos(ms * NS);
            to = to.plusNanos(ms * NS);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(id).append("\n");
            sb.append(formatter(from)).append(" --> ").append(formatter(to)).append("\n");
            text.forEach(l -> sb.append(l).append("\n"));
            return sb.toString();
        }
    }

    static class Subtitles {
        private final List<Part> partList;

        Subtitles() {
            partList = new ArrayList<>();
        }

        int loadSubtitles() {
            Scanner sc = new Scanner(System.in);
            while (sc.hasNext()) {
                Part ins;
                String id = sc.nextLine();

                String[] l = sc.nextLine().split(" --> ");
                LocalTime from = Part.parser(l[0]);
                LocalTime to = Part.parser(l[1]);

                List<String> text = new ArrayList<>();
                while (true) {
                    String line;
                    if (!sc.hasNext() || (line = sc.nextLine()).isEmpty()) {
                        ins = new Part(id, from, to, text);
                        break;
                    }
                    text.add(line);
                }
                partList.add(ins);
            }

            return partList.size();
        }

        void print() {
            partList.forEach(System.out::println);
        }

        void shift(int ms) {
            partList.forEach(p -> p.shift(ms));
        }
    }
}
