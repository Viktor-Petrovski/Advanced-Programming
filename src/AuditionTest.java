import java.util.*;

public class AuditionTest {
    public static void main(String[] args) {
        Audition audition = new Audition();
        List<String> cities = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            if (parts.length > 1) {
                audition.addParticipant(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } else {
                cities.add(line);
            }
        }
        for (String city : cities) {
            System.out.printf("+++++ %s +++++\n", city);
            audition.listByCity(city);
        }
        scanner.close();
    }

    static class Participant {
        private final String code;
        private final String name;
        private final int age;

        Participant(String code, String name, int age) {
            this.code = code;
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return String.format("%s %s %d", code, name, age);
        }
    }

    static class Audition {
        private final Map<String, Map<String, Participant>> cityMap; // city -> map<id, participant>

        Audition() {
            cityMap = new HashMap<>();
        }

        // Во ист град не се дозволува додавање на кандидат со ист код како некој претходно додаден кандидат
        // (додавањето се игнорира, а комплексноста на овој метод треба да биде O(1))
        void addParticipant(String city, String code, String name, int age) {
            Map<String, Participant> map = cityMap.computeIfAbsent(city, k -> new HashMap<>());
            map.putIfAbsent(code, new Participant(code, name, age));
        }


        //ги печати сите кандидати од даден град подредени според името, а ако е исто според возраста
        // (комплексноста на овој метод не треба да надминува O(n*log_2(n)), каде n е бројот на кандидати во дадениот град).
        void listByCity(String city) {
            cityMap.get(city).values().stream()
                    .sorted(Comparator.comparing(Participant::getName).thenComparing(Participant::getAge))
                    .forEach(System.out::println);
        }

    }
}