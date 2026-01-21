import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClusterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cluster<Point2D> cluster = new Cluster<>();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            long id = Long.parseLong(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            cluster.addItem(new Point2D(id, x, y));
        }
        int id = scanner.nextInt();
        int top = scanner.nextInt();
        cluster.near(id, top);
        scanner.close();
    }

    @FunctionalInterface
    interface Identifiable {
        long getId();
    }

    interface Clusterable {
        double getDistance(Clusterable o);

        double getX();

        double getY();
    }

    static class Point2D implements Identifiable, Clusterable{
        private final long id;
        private final double x;
        private final double y;

        Point2D(long id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public double getDistance(Clusterable o) {
            return Math.sqrt(
                    Math.pow(this.getX() - o.getX(), 2) +
                    Math.pow(this.getY() - o.getY(), 2)
            );
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

    }

    static class Cluster<T extends Identifiable & Clusterable> {
        private final List<T> list;

        Cluster() {
            list = new ArrayList<>();
        }

        void addItem(T element) {
            list.add(element);
        }

        private T findElement(long id) {
            return list.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
        }


        void near(long id, int top) {
            T centroid = findElement(id);

            List<T> res = list.stream()
                    .filter(i -> i.getId() != id)
                    .sorted(Comparator.comparingDouble(i -> i.getDistance(centroid)))
                    .collect(Collectors.toCollection(ArrayList::new));

            IntStream.range(1, top + 1).forEach(i -> {
                T curr = res.get(i - 1);
                System.out.printf("%d. %s -> %.3f\n", i, curr.getId(), curr.getDistance(centroid));
            });
        }
    }
}
