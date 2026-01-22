import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class StopCoronaTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StopCoronaApp stopCoronaApp = new StopCoronaApp();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "REG":
                    try {
                        stopCoronaApp.addUser(parts[1], parts[2]);
                    } catch (UserIdAlreadyExistsException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "LOC":
                    String id = parts[1];
                    List<ILocation> locations = new ArrayList<>();
                    for (int i = 2; i < parts.length; i += 3) {
                        locations.add(createLocationObject(parts[i], parts[i + 1], parts[i + 2]));
                    }
                    stopCoronaApp.addLocations(id, locations);
                    break;
                case "DET":
                    stopCoronaApp.detectNewCase(parts[1], LocalDateTime.parse(parts[2]));
                    break;
                case "REP":
                    stopCoronaApp.createReport();
                    break;
            }
        }
    }

    private static ILocation createLocationObject(String lon, String lat, String timestamp) {
        return new ILocation() {
            public double getLongitude() { return Double.parseDouble(lon); }
            public double getLatitude() { return Double.parseDouble(lat); }
            public LocalDateTime getTimestamp() { return LocalDateTime.parse(timestamp); }
        };
    }

    interface ILocation {
        double getLongitude();
        double getLatitude();
        LocalDateTime getTimestamp();
    }

    static class UserIdAlreadyExistsException extends Exception {
        public UserIdAlreadyExistsException(String id) {
            super(String.format("User with id %s already exists", id));
        }
    }

    static class User {
        private final String id;
        private final String name;
        private final List<ILocation> locations = new ArrayList<>();

        User(String id, String name) {
            this.id = id;
            this.name = name;
        }

        void addLocations(List<ILocation> locs) {
            locations.addAll(locs);
        }

        String getId() { return id; }
        String getName() { return name; }
        List<ILocation> getLocations() { return locations; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User)) return false;
            return id.equals(((User) o).id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        static final Comparator<User> BY_NAME_THEN_ID =
                Comparator.comparing(User::getName).thenComparing(User::getId);
    }

    static class StopCoronaApp {
        private final Map<String, User> users = new HashMap<>();
        private final Map<String, LocalDateTime> sickUsers = new HashMap<>();

        void addUser(String name, String id) throws UserIdAlreadyExistsException {
            if (users.containsKey(id))
                throw new UserIdAlreadyExistsException(id);
            users.put(id, new User(id, name));
        }

        void addLocations(String id, List<ILocation> locs) {
            users.get(id).addLocations(locs);
        }

        void detectNewCase(String id, LocalDateTime timestamp) {
            sickUsers.put(id, timestamp);
        }

        private boolean isClose(ILocation a, ILocation b) {
            double dist = Math.sqrt(
                    Math.pow(a.getLatitude() - b.getLatitude(), 2) + Math.pow(a.getLongitude() - b.getLongitude(), 2)
            );
            long mins = Math.abs(Duration.between(a.getTimestamp(), b.getTimestamp()).toSeconds());
            return dist <= 2 && mins <= 300;
        }

        private int countContacts(User carrier, User other) {
            int count = 0;

            for (ILocation cLoc : carrier.getLocations())
                for (ILocation oLoc : other.getLocations())
                    if (isClose(cLoc, oLoc))
                        count++;

            return count;
        }


        Map<User, Integer> getDirectContacts(User u) {
            return users.values().stream()
                    .filter(x -> !x.getId().equals(u.getId()))
                    .map(x -> Map.entry(x, countContacts(u, x)))
                    .filter(e -> e.getValue() > 0)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        Collection<User> getIndirectContacts(User u) {
            Map<User, Integer> direct = getDirectContacts(u);
            return direct.keySet().stream()
                    .flatMap(d -> getDirectContacts(d).keySet().stream())
                    .filter(x -> !x.getId().equals(u.getId()))
                    .filter(x -> !direct.containsKey(x))
                    .distinct()
                    .sorted(User.BY_NAME_THEN_ID)
                    .collect(Collectors.toList());
        }

        void createReport() {
            List<User> sickOrdered = sickUsers.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(e -> users.get(e.getKey()))
                    .collect(Collectors.toCollection(ArrayList::new));

            for (User u : sickOrdered) {
                LocalDateTime detected = sickUsers.get(u.getId());
                Map<User, Integer> direct = getDirectContacts(u);
                Collection<User> indirect = getIndirectContacts(u);

                System.out.printf("%s %s %s%n", u.getName(), u.getId(), detected);
                System.out.println("Direct contacts:");


                direct.entrySet().stream()
                        .sorted(
                                Map.Entry.<User, Integer>comparingByValue(Comparator.reverseOrder())
                                        .thenComparing(Map.Entry.comparingByKey(User.BY_NAME_THEN_ID))
                        )
                        .forEach(e -> System.out.printf(
                                "%s %s*** %d%n",
                                e.getKey().getName(),
                                e.getKey().getId().substring(0, 4),
                                e.getValue()
                        ));

                int directCount = direct.values().stream().mapToInt(i -> i).sum();
                System.out.printf("Count of direct contacts: %d%n", directCount);

                System.out.println("Indirect contacts:");
                indirect.forEach(x ->
                        System.out.printf("%s %s***%n", x.getName(), x.getId().substring(0, 4))
                );

                System.out.printf("Count of indirect contacts: %d%n", indirect.size());
            }


            double totalDirect = sickOrdered.stream()
                    .mapToInt(u -> getDirectContacts(u).values().stream().mapToInt(i -> i).sum())
                    .sum();

            double totalIndirect = sickOrdered.stream()
                    .mapToInt(u -> getIndirectContacts(u).size())
                    .sum();


            if (!sickOrdered.isEmpty()) {
                System.out.printf("Average direct contacts: %.4f%n", totalDirect / sickOrdered.size());
                System.out.printf("Average indirect contacts: %.4f%n", totalIndirect / sickOrdered.size());
            }
        }

    }
}
