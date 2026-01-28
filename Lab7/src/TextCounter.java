import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class TextCounter {

    // Result holder
    public static class Counter {
        public final int textId;
        public final int lines;
        public final int words;
        public final int chars;

        public Counter(int textId, int lines, int words, int chars) {
            this.textId = textId;
            this.lines = lines;
            this.words = words;
            this.chars = chars;
        }

        @Override
        public String toString() {
            return "Counter{" +
                    "textId=" + textId +
                    ", lines=" + lines +
                    ", words=" + words +
                    ", chars=" + chars +
                    '}';
        }


    }


    /// Овој метод мора да враќа Callable<Counter>. Не е дозволено да креирате посебна класа што имплементира Callable;
    /// задолжително е да користите ламбда-израз. Пресметката на статистиките за текстот мора да се извршува внатре во Callable,
    /// а не пред неговото креирање. Кога callable-задачата ќе се изврши, таа треба да го изброи бројот на линии во текстот,
    /// бројот на зборови (при што зборовите се разделени со еден или повеќе празнини) и бројот на карактери во текстот.
    /// Callable-задачата мора да врати нов објект од тип Counter, инициализиран со дадениот textId и пресметаните вредности
    public static Callable<Counter> getTextCounter(int textId, String text) {
        return () -> {
            int lines = text.split("\n").length;
            int words = text.split("\\s+").length;
            int chars = text.length();

            return new Counter(textId, lines, words, chars);
        };
    }



    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();       // number of texts
        sc.nextLine();              // consume newline

        List<Callable<Counter>> tasks = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int textId = sc.nextInt();
            sc.nextLine();          // consume newline

            int lines = sc.nextInt();   // number of lines for this text
            sc.nextLine();              // consume newline

            StringBuilder text = new StringBuilder();
            for (int j = 0; j < lines; j++) {
                text.append(sc.nextLine());
                if (j < lines - 1) {
                    text.append("\n");
                }
            }

            tasks.add(getTextCounter(textId, text.toString()));
            //TODO add a Callable<Counter> for each text read in the tasks list
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        //TODO invoke All tasks on the executor and create a List<Future<?>>
        List<Future<Counter>> futures = executor.invokeAll(tasks);

        List<Counter> results = new ArrayList<>();

        futures.forEach(f -> {
            try {
                Counter counter = f.get();
                results.add(counter);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        //TODO extract results from the List<Future>

        executor.shutdown();


        // Sorting by textId (important concept!)
        results.sort(Comparator.comparingInt(c -> c.textId));

        // Output (optional for debugging / demonstration)
        for (Counter c : results) {
            System.out.printf(
                    "%d %d %d %d%n",
                    c.textId, c.lines, c.words, c.chars
            );
        }
    }
}
