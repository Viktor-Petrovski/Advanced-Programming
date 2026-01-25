import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class TermFrequencyTest {
    public static void main(String[] args) {
        String[] stop = new String[] { "во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја" };
        TermFrequency tf = new TermFrequency(stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften());
    }

    static String clean(String in) {
        StringBuilder sb = new StringBuilder();
        for (char c : in.toCharArray()) {
            if (Character.isAlphabetic(c) || Character.isDigit(c))
                sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    static class TermFrequency {
        private final Map<String, Integer> map; // word -> occurrences
        private final Set<String> includedStop;

        TermFrequency(String[] stop) {
            Set<String> stopWords = Arrays.stream(stop).collect(Collectors.toCollection(HashSet::new));
            map = new HashMap<>();
            includedStop = new TreeSet<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            br.lines().flatMap(l -> Arrays.stream(l.split("\\s+")))
                    .map(TermFrequencyTest::clean)
                    .filter(w -> { // овој филтер само за дополнителното барање го додадив (да не користам терминална операција)
                        if (stopWords.contains(w))
                            includedStop.add(w);
                        return true;
                    })
                    .filter(w -> !stopWords.contains(w))
                    .filter(w -> !w.isEmpty())
                    .forEach(w -> map.merge(w, 1, Integer::sum));
        }

        /// вкупниот број на зборови во текстот
        int countTotal() {
            return map.values().stream().mapToInt(i -> i).sum();
        }

        /// вкупниот број на уникатни зборови
        int countDistinct() {
            return map.size();
        }

        /// враќа листа која ги содржи k-те зборови кои најчесто се појавуваат во текстот подредени
        /// според бројот на појавување од најмногу до најмалку. Во случај на ист број на појавувања
        /// се подредуваат алфабетски
        List<String> mostOften() {
            Comparator<Map.Entry<String, Integer>> byVal = Map.Entry.comparingByValue();
            Comparator<Map.Entry<String, Integer>> byKey = Map.Entry.comparingByKey();

            return map.entrySet().stream().sorted(byVal.reversed().thenComparing(byKey))
                    .limit(10)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        /// враќа мапа каде што клуч е бројот на појавувања на зборовите во текстот,
        /// а вредност е листа од зборови кои се појавуваат точно толку пати.
        /// Листите на зборови треба да бидат подредени азбучно, а фреквенциите треба да бидат подредени опаѓачки
        public Map<Integer, List<String>> byFrequency() {
            return map.entrySet().stream()
                    .collect(Collectors.groupingBy(
                            Map.Entry::getValue,
                            () -> new TreeMap<Integer, List<String>>(Comparator.reverseOrder()),
                            Collectors.mapping(
                                    Map.Entry::getKey,
                                    Collectors.toList()
                            )
                    ));
        }

        /// кој ќе враќа сет од сите стоп-зборови кои навистина се појавиле во текстот (иако не се бројат во статистиката).
        public Set<String> stopWordsUsed() {
            return includedStop;
        }

        /// кој ќе го враќа најдолгиот збор што се појавува во текстот.
        /// Во случај повеќе зборови да се со иста должина, да се врати оној што е лексикографски најмал.
        public String longestWord() {
            return map.keySet().stream()
                    .max(Comparator.comparingInt(String::length).thenComparing(Comparator.reverseOrder()))
                    .orElse("");
        }

        /// кој ќе враќа мапа каде што клуч е првата буква од зборовите,
        /// а вредност е листа од зборовите што започнуваат со таа буква.
        /// Листите треба да бидат подредени азбучно.
        public Map<Character, List<String>> groupByFirstLetter() {
            return map.keySet().stream().collect(Collectors.groupingBy(
                    s -> s.charAt(0),
                    TreeMap::new,
                    Collectors.collectingAndThen(
                            Collectors.toCollection(TreeSet::new),
                            ArrayList::new
                    )
            ));
        }

        /// кој ќе враќа мапа каде што клуч е префикс од дадена должина,
        /// а вредност е бројот на зборови во текстот кои започнуваат со тој префикс.
        /// Зборовите пократки од префиксот се игнорираат.
        public Map<String, Integer> countPrefixes(int prefixLength) {
            return map.entrySet().stream()
                    .filter(e -> e.getKey().length() >= prefixLength)
                    .collect(Collectors.groupingBy(
                            e -> e.getKey().substring(0, prefixLength), Collectors.summingInt(Map.Entry::getValue)
                            )
                    );
        }

        /// кој ќе прави инверзија на мапата со фреквенции, така што фреквенцијата ќе биде клуч,
        /// а сет од зборови со таа фреквенција ќе биде вредност. Сетовите треба да бидат подредени азбучно.
        public Map<Integer, Set<String>> invertIndex() {
            return map.entrySet().stream().collect(Collectors.groupingBy(
                    Map.Entry::getValue,
                    TreeMap::new,
                    Collectors.mapping(
                            Map.Entry::getKey,
                            Collectors.toCollection(TreeSet::new)
                    )
            ));
        }

    }
}
