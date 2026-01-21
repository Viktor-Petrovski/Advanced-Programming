import java.util.*;
import java.util.stream.Collectors;

public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }

    static class Names {
        private final Map<String, Integer> nameMap; // name -> occurrences

        Names() {
            nameMap = new TreeMap<>();
        }

        public void addName(String name) {
            nameMap.putIfAbsent(name, 0);
            nameMap.put(name, nameMap.get(name) + 1);
        }

        private int uniqueChars(String s) {
            Set<Character> set = new HashSet<>();
            for (char c : s.toCharArray())
                set.add(Character.toLowerCase(c));
            return set.size();
        }

        public void printN(int n) {
            nameMap.entrySet().stream().filter(e -> e.getValue() >= n)
                    .forEach(e -> System.out.printf("%s (%d) %d\n",
                            e.getKey(),
                            e.getValue(),
                            uniqueChars(e.getKey()
                            )));
        }

        public String findName(int len, int x) {
            List<String> filtered = nameMap.keySet().stream().filter(k -> k.length() < len)
                    .collect(Collectors.toCollection(ArrayList::new));

            x %= filtered.size();
            return filtered.get(x);
        }
    }
}

