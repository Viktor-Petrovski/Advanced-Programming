import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DocumentViewerTest {
    public static void main(String[] args) {
        DocumentViewer viewer = new DocumentViewer();
        Scanner sc = new Scanner(System.in);
        int n = Integer.parseInt(sc.nextLine());

        IntStream.range(0, n).forEach(i -> {
            String id = sc.nextLine();
            int m = Integer.parseInt(sc.nextLine());
            StringBuilder text = new StringBuilder();

            IntStream.range(0, m).forEach(j ->
                text.append(sc.nextLine()).append("\n")
            );

            viewer.addDocument(id, text.toString());
        });

        while (true) {
            String in = sc.nextLine();
            if (in.equalsIgnoreCase("exit"))
                break;
            viewer.action(in);
        }
    }

    @FunctionalInterface
    interface Document {
        String getText();
    }

    static class SimpleDocument implements Document {
        private final String id;
        private final String text;

        SimpleDocument(String id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public String getText() {
            return String.format("=== Document %s ===\n%s", id, text);
        }
    }

    static abstract class Decorator implements Document {
        protected final Document document;

        Decorator(Document document) {
            this.document = document;
        }
    }

    static class LineNumbersDecorator extends Decorator {

        LineNumbersDecorator(Document document) {
            super(document);
        }

        @Override
        public String getText() {
            String[] txt = document.getText().split("\n");

            StringBuilder sb = new StringBuilder(txt[0] + "\n");

            IntStream.range(1, txt.length).forEach(i ->
                sb.append(String.format("%d: %s\n", i, txt[i]))
            );
            return sb.toString();
        }
    }

    static class WordCountDecorator extends Decorator {
        WordCountDecorator(Document document) {
            super(document);
        }

        @Override
        public String getText() {
            String txt = Arrays.stream(document.getText().split("\n"))
                    .skip(1).collect(Collectors.joining(" "));

            long words = txt.split("\\s+").length;
            return document.getText() + "Words: " + words + "\n";
        }
    }

    static class RedactionDecorator extends Decorator {
        private final List<String> forbiddenWords;

        RedactionDecorator(Document document, List<String> forbiddenWords) {
            super(document);
            this.forbiddenWords = forbiddenWords;
        }

        @Override
        public String getText() {
            String text = document.getText();
            for (String forbiddenWord : forbiddenWords) {
                String regex = "(?i)\\b" + Pattern.quote(forbiddenWord) + "\\b";
                text = text.replaceAll(regex, "*");
            }
            return text;
        }
    }

    static class DocumentViewer {
        private final Map<String, Document> documentMap;

        DocumentViewer() {
            documentMap = new HashMap<>();
        }

        /// метод за додавање на нов документ со ИД id и содржина text. Документите во себе содржат повеќе редови текст (одделени со \n)
        void addDocument(String id, String text) {
            documentMap.putIfAbsent(id, new SimpleDocument(id, text));
        }

        /// метод за нумерирање на секоја линија во документот со реден број пред линијата
        /// (пр. 1. на почеток на првата линија, 2. на почеток на втората линија итн.)
        private void enableLineNumbers(String id) {
            documentMap.computeIfPresent(id, (k, v) -> new LineNumbersDecorator(v));
        }

        /// методот за додавање на нов ред во документот со содржина "Words: W", каде што W е бројот на зборови во цел документ
        private void enableWordCount(String id) {
            documentMap.computeIfPresent(id, (k, v) -> new WordCountDecorator(v));
        }

        /// методот кој ќе ги редактира (замени со *) сите зборови од листата forbiddenWords во документот
        private void enableRedaction(String id, List<String> forbiddenWords) {
            documentMap.computeIfPresent(id, (k, v) -> new RedactionDecorator(v, forbiddenWords));
        }

        /// метод кој ќе го испечати документот на стандарден излез
        private void display(String id) {
            String text = documentMap.get(id).getText();
            System.out.print(text);
        }

        public void action(String action) {
            String[] tokens = action.split(" ");
            String command = tokens[0];
            String id = tokens[1];

            if (command.equalsIgnoreCase("enableLineNumbers"))
                enableLineNumbers(id);
            if (command.equalsIgnoreCase("enableWordCount"))
                enableWordCount(id);
            if (command.equalsIgnoreCase("display"))
                display(id);
            if (command.equalsIgnoreCase("enableRedaction"))
                enableRedaction(id, Arrays.stream(tokens)
                        .skip(2).collect(Collectors.toCollection(ArrayList::new)));

        }
    }
}
