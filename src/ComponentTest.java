import java.util.*;

public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    prev = new Component(color, weight);
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    assert prev != null;
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    assert prev != null;
                    prev.addComponent(component);
                } else if (what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.println(String.format("=== CHANGED COLOR (%d, %s) ===", weight, color));
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.println(String.format("=== SWITCHED COMPONENTS %d <-> %d ===", pos1, pos2));
        window.switchComponents(pos1, pos2);
        System.out.println(window);
    }

    static class InvalidPositionException extends Exception {
        public InvalidPositionException(int pos) {
            super(String.format("Invalid position %d, alredy taken!", pos));
        }
    }

    static class Component implements Comparable<Component> {

        private String color;
        private final int weight;
        private final Set<Component> componentSet;
        Component(String color, int weight) {
            this.color = color;
            this.weight = weight;
            componentSet = new TreeSet<>();
        }

        @Override
        public int compareTo(Component o) {
            int i = Integer.compare(weight, o.weight);
            if (i != 0) return i;
            return color.compareTo(o.color);
        }

        void addComponent(Component ins) {
            componentSet.add(ins);
        }

        void changeIfWeightSmallerThanRecursive(int weight, String color) {
            if (this.weight < weight)
                this.color = color;
            componentSet.forEach(c -> c.changeIfWeightSmallerThanRecursive(weight, color));
        }

        String info(int lvl) {
            StringBuilder sb = new StringBuilder();
            sb.append("---".repeat(lvl)).append(weight).append(":").append(color).append("\n");
            componentSet.forEach(c -> sb.append(c.info(lvl + 1)));
            return sb.toString();
        }

    }

    static class Window {
        private final String name;
        private final Map<Integer, Component> componentMap;

        Window(String name) {
            this.name = name;
            componentMap = new TreeMap<>();
        }

        /// додава нова компонента на дадена позиција (цел број).
        /// На секоја позиција може да има само една компонента,
        /// ако се обидеме да додадеме компонента на зафатена позиција треба да се фрли исклучок
        ///  од класата InvalidPositionException со порака Invalid position \[pos\], already taken!.
        ///  Компонентите се подредени во растечки редослед според позицијата.
        void addComponent(int position, Component component) throws InvalidPositionException {
            if (componentMap.containsKey(position))
                throw new InvalidPositionException(position);
            componentMap.put(position, component);
        }

        /// ја менува бојата на сите компоненти со тежина помала од проследената
        void changeColor(int weight, String color) {
            componentMap.values().forEach(c -> c.changeIfWeightSmallerThanRecursive(weight, color));
        }

        /// ги заменува компонените од проследените позиции.
        void switchComponents(int pos1, int pos2) {
            Component x = componentMap.get(pos1);
            Component y = componentMap.get(pos2);

            componentMap.put(pos2, x);
            componentMap.put(pos1, y);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("WINDOW %s\n", name));

            componentMap.forEach((k, v) -> sb.append(String.format("%d:%s", k, v.info(0))));

            return sb.toString();
        }
    }

}

