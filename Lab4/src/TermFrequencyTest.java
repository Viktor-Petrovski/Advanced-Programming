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

        TermFrequency(String[] stop) {
            Set<String> stopWords = Arrays.stream(stop).collect(Collectors.toCollection(HashSet::new));
            map = new HashMap<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            br.lines().flatMap(l -> Arrays.stream(l.split("\\s+")))
                    .map(TermFrequencyTest::clean)
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
    }
}
