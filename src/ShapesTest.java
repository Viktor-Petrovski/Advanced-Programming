import java.util.*;


public class ShapesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Canvas canvas = new Canvas();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            int type = Integer.parseInt(parts[0]);
            String id = parts[1];
            if (type == 1) {
                Color color = Color.valueOf(parts[2]);
                float radius = Float.parseFloat(parts[3]);
                canvas.add(id, color, radius);
            } else if (type == 2) {
                Color color = Color.valueOf(parts[2]);
                float width = Float.parseFloat(parts[3]);
                float height = Float.parseFloat(parts[4]);
                canvas.add(id, color, width, height);
            } else if (type == 3) {
                float scaleFactor = Float.parseFloat(parts[2]);
                System.out.println("ORIGNAL:");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf("AFTER SCALING: %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }

        }
    }

    enum Color {
        RED, GREEN, BLUE
    }

    @FunctionalInterface
    interface Scalable {
        /// за соодветно зголемување/намалување на формата за дадениот фактор
        void scale(float scaleFactor);
    }

    @FunctionalInterface
    interface Stackable {
        /// кој враќа тежината на формата (се пресметува како плоштина на соодветната форма)
        float weight();
    }

    abstract static class Shape implements Scalable, Stackable {
        protected final String id;
        protected final Color color;
        private static final Comparator<Shape> BY_WEIGHT_DESC_THEN_ID =
                Comparator.comparing(Shape::weight).reversed().thenComparing(Shape::getId);

        Shape(String id, Color color) {
            this.id = id;
            this.color = color;
        }

        public String getId() {
            return id;
        }
    }

    static class Circle extends Shape {
        private float radius;

        Circle(String id, Color color, float radius) {
            super(id, color);
            this.radius = radius;
        }

        @Override
        public void scale(float scaleFactor) {
            radius *= scaleFactor;
        }

        @Override
        public float weight() {
            return (float) (radius * radius * Math.PI);
        }

        @Override
        public String toString() {
            // C: [id:5 места од лево] [color:10 места од десно] [weight:10.2 места од десно]
            // C: c1   RED           706.86
            return String.format("C: %-5s%-10s%10.2f", id, color, weight());
        }
    }

    static class Rectangle extends Shape {
        private float width;
        private float height;

        Rectangle(String id, Color color, float width, float height) {
            super(id, color);
            this.width = width;
            this.height = height;
        }

        @Override
        public void scale(float scaleFactor) {
            width *= scaleFactor;
            height *= scaleFactor;
        }

        @Override
        public float weight() {
            return width * height;
        }

        @Override
        public String toString() {
            // R: [id:5 места од лево] [color:10 места од десно] [weight:10.2 места од десно]
            // R: r2   GREEN          80.00
            return String.format("R: %-5s%-10s%10.2f", id, color, weight());
        }
    }

    static class Canvas {
        private final List<Shape> shapeList;

        Canvas() {
            shapeList = new ArrayList<>();
        }

        private Shape findById(String id) {
            return shapeList.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
        }

        private void insertShape(Shape ins) {
            int index = Collections.binarySearch(shapeList, ins,
                    Shape.BY_WEIGHT_DESC_THEN_ID);

            if (index < 0)
                index = -(index + 1);

            shapeList.add(index, ins);
        }

        void add(String id, Color color, float radius) {
            insertShape(new Circle(id, color, radius));
        }

        void add(String id, Color color, float width, float height) {
            insertShape(new Rectangle(id, color, width, height));
        }

        void scale(String id, float scaleFactor) {
            Shape scaled = findById(id);
            scaled.scale(scaleFactor);

            shapeList.remove(scaled);
            insertShape(scaled);
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();
            shapeList.forEach(s -> sb.append(s.toString()).append("\n"));
            return sb.toString();
        }

    }
}

