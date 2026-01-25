import java.util.*;
import java.util.stream.Collectors;


public class BucketTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // bucket name is fixed
        Bucket bucket = new Bucket("bucket");

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String command = parts[0];

            if (command.equalsIgnoreCase("ADD")) {
                bucket.addObject(parts[1]);
            } else if (command.equalsIgnoreCase("REMOVE")) {
                bucket.removeObject(parts[1]);
            } else if (command.equalsIgnoreCase("PRINT")) {
                System.out.print(bucket.toString(0));
            }
        }
    }

    static class Bucket {
        private final String name;
        private final Map<String, Bucket> bucketMap;

        Bucket(String name) {
            this.name = name;
            bucketMap = new LinkedHashMap<>();
        }

        private Map<String, Bucket> getBucketMap() {
            return bucketMap;
        }

        void addObject(String key) {
            String[] tokens = key.split("/");
            String currentDirectory = tokens[0];
            bucketMap.computeIfAbsent(currentDirectory, Bucket::new);

            if (tokens.length > 1) {
                String k = Arrays.stream(tokens).skip(1).collect(Collectors.joining("/"));
                bucketMap.get(currentDirectory).addObject(k);
            }
        }

        void removeObject(String key) {
            String[] tokens = key.split("/");
            String currentDirectory = tokens[0];
            if (tokens.length == 1)
                bucketMap.remove(currentDirectory);
            else {
                String k = Arrays.stream(tokens).skip(1).collect(Collectors.joining("/"));
                bucketMap.get(currentDirectory).removeObject(k);
                if (bucketMap.get(currentDirectory).getBucketMap().isEmpty())
                    bucketMap.remove(currentDirectory);
            }
        }

        public String toString(int lvl) {
            StringBuilder sb = new StringBuilder();
            String cross = !bucketMap.isEmpty() || name .equalsIgnoreCase("bucket") ? "/" : "";
            sb.append("    ".repeat(lvl)).append(name).append(cross).append("\n");

            bucketMap.values().forEach(b -> sb.append(b.toString(lvl + 1)));

            return sb.toString();
        }
    }
}

