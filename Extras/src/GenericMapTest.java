import java.util.*;
import java.util.stream.Collectors;

public class GenericMapTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { //Mergeable integers
            Map<Integer, Integer> mapLeft = new HashMap<>();
            Map<Integer, Integer> mapRight = new HashMap<>();
            readIntMap(sc, mapLeft);
            readIntMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging of
            // two Integer objects into a new Integer object which is their sum
            MergeStrategy<Integer> mergeStrategy = (left, right) -> {
                Map<Integer, Integer> res = new HashMap<>(left);
                right.forEach((key, value) -> res.merge(key, value, Integer::sum));
                return res;
            };

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 2) { // Mergeable strings
            Map<String, String> mapLeft = new HashMap<>();
            Map<String, String> mapRight = new HashMap<>();
            readStrMap(sc, mapLeft);
            readStrMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging of
            // two String objects into a new String object which is their concatenation
            MergeStrategy<String> mergeStrategy = (left, right) -> {
                Map<String, String> res = new HashMap<>(left);
                right.forEach((key, value) -> res.merge(key, value, String::concat));
                return res;
            };

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 3) {
            Map<Integer, Integer> mapLeft = new HashMap<>();
            Map<Integer, Integer> mapRight = new HashMap<>();
            readIntMap(sc, mapLeft);
            readIntMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging of
            // two Integer objects into a new Integer object which will be the max of the two objects
            MergeStrategy<Integer> mergeStrategy = (left, right) -> {
                Map<Integer, Integer> res = new HashMap<>(left);
                right.forEach((key, value) -> res.merge(key, value, Math::max));
                return res;
            };

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 4) {
            Map<String, String> mapLeft = new HashMap<>();
            Map<String, String> mapRight = new HashMap<>();
            readStrMap(sc, mapLeft);
            readStrMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging of
            // two String objects into a new String object which will mask the occurrences of the second string in the first string

            MergeStrategy<String> mergeStrategy = (left, right) -> {
                Map<String, String> res = new HashMap<>(left);
                right.forEach((key, value) -> res.merge(key, value, GenericMapTest::mask));
                return res;
            };
            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        }
    }
    
    //A -> NP
    //B -> ***Ki
    //C -> **P
    //D -> ke_polozam_**
    //E -> 2022/2023
    
    //A -> NP
    //B -> FINKiFIN
    //C -> NPPNP
    //D -> ke_polozam_NPNP
    //E -> 2022/2023

    private static String mask(String x, String y) {
        return x.replaceAll("(?i)" + y, "*".repeat(y.length()));
    }

    private static void readIntMap(Scanner sc, Map<Integer, Integer> map) {
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < n; i++) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            int k = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);
            map.put(k, v);
        }
    }

    private static void readStrMap(Scanner sc, Map<String, String> map) {
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < n; i++) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            map.put(parts[0], parts[1]);
        }
    }

    private static void printMap(Map<?, ?> map) {
        map.forEach((k, v) -> System.out.printf("%s -> %s%n", k.toString(), v.toString()));
    }

    @FunctionalInterface
    interface MergeStrategy <T extends Comparable<T>> {
        Map<T, T> merge(Map<T, T> left, Map<T, T> right);
    }

    /// Мапите се спојуваат на следниот начин:
    /// Ако определен клуч од втората мапа постои во првата мапа, резултатот ќе биде нов елемент/пар во
    /// резултантната мапа од истиот тој клуч и вредност која се добива со спојување на вредностите од
    /// првата и втората мапа согласно логиката во стратегијата за спојување.
    ///
    /// Сите клучеви кои се јавуваат точно еднаш во првата и втората мапа, се додаваат во
    /// резултантната мапа со нивната вредност без промени.
    static class MapOps {
        static <T extends Comparable<T>> Map<T, T> merge(Map<T, T> left, Map<T, T> right, MergeStrategy<T> strategy) {
            return strategy.merge(left, right)
                    .entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> x, TreeMap::new));
        }
    }
}
