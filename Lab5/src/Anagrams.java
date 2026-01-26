import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Anagrams {
    private static final Map<String, TreeSet<String>> map = new HashMap<>(); // anagram -> og words

    public static String anagram(String s) {
        String[] chars = s.split("");
        return Arrays.stream(chars).sorted().collect(Collectors.joining(""));
    }

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        br.lines().forEach(s -> map.computeIfAbsent(anagram(s), k -> new TreeSet<>()).add(s));

        map.entrySet().stream()
                .filter(e -> e.getValue().size() >= 5)
                .sorted(Comparator.comparing(e -> e.getValue().first()))
                .forEach(e -> System.out.println(String.join(" ", e.getValue())));
    }
}
