import java.io.*;
import java.util.*;

public class CanvasTest {

    public static void main(String[] args) {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        canvas.readShapes();

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes();
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes();

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId();

        System.out.println("PRINT STATISTICS");
        canvas.statistics();
    }

    static class InvalidIDException extends Exception {
        public InvalidIDException(String username) {
            super(String.format("ID %s is not valid", username));
        }
    }

    static class InvalidDimensionException extends Exception {
        public InvalidDimensionException() {
            super("Dimension 0 is not allowed!");
        }
    }

    static abstract class Shape implements Comparable<Shape> {
        abstract double area();

        abstract double perimeter();

        abstract void scale(double coefficient);

        @Override
        public int compareTo(Shape o) {
            return Double.compare(this.area(), o.area());
        }
    }

    static class Circle extends Shape {
        private double radius;

        Circle(double radius) {
            this.radius = radius;
        }


        @Override
        public double area() {
            return radius * radius * Math.PI;
        }

        @Override
        public double perimeter() {
            return 2 * radius * Math.PI;
        }

        @Override
        public void scale(double coefficient) {
            radius *= coefficient;
        }

        @Override
        public String toString() {
            return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f", radius, area(), perimeter());
        }
    }

    static class Square extends Shape {
        private double side;

        Square(double side) {
            this.side = side;
        }

        @Override
        public double area() {
            return side * side;
        }

        @Override
        public double perimeter() {
            return side * 4;
        }

        @Override
        public void scale(double coefficient) {
            side *= coefficient;
        }

        @Override
        public String toString() {
            return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f", side, area(), perimeter());
        }

    }

    static class Rectangle extends Shape {
        private double width;
        private double height;

        Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public double area() {
            return width * height;
        }

        @Override
        public double perimeter() {
            return 2 * width + 2 * height;
        }

        @Override
        public void scale(double coefficient) {
            width *= coefficient;
            height *= coefficient;
        }

        @Override
        public String toString() {
            return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f", width, height, area(), perimeter());
        }

    }

    static class ShapeFactory {
        static Shape create(String s) throws InvalidDimensionException {
            String[] tokens = s.split("\\s+");
            int i = tokens.length - 1;

            if (Double.parseDouble(tokens[i]) == .0)
                throw new InvalidDimensionException();

            if (s.startsWith("1"))
                return new Circle(Double.parseDouble(tokens[i]));

            if (s.startsWith("2"))
                return new Square(Double.parseDouble(tokens[i]));

            if (s.startsWith("3")) {
                if (Double.parseDouble(tokens[i - 1]) == .0)
                    throw new InvalidDimensionException();
                return new Rectangle(Double.parseDouble(tokens[i - 1]), Double.parseDouble(tokens[i]));
            }

            return null;
        }
    }

    static class Canvas {
        private static final Scanner sc;
        private static final PrintWriter pw;

        static {
            sc = new Scanner(System.in);
            pw = new PrintWriter(System.out);
        }

        private final Map<String, Collection<Shape>> userMap; // user -> shapes
        private final Collection<Shape> allShapes; // user -> shapes

        Canvas() {
            userMap = new HashMap<>();
            allShapes = new TreeSet<>();
        }

        private void isValidUserID(String name) throws InvalidIDException {
            int VALID_SIZE = 6;
            if (!(name.length() == VALID_SIZE && name.matches("[A-Za-z0-9]+")))
                throw new InvalidIDException(name);
        }

        void readShapes() {
            String s;
            while (true) {
                if (!sc.hasNext() || (s = sc.nextLine()).isEmpty())
                    break;

                try {
                    String[] tokens = s.split("\\s+");
                    String userID = tokens[1];
                    isValidUserID(userID);

                    Shape ins = ShapeFactory.create(s);
                    userMap.computeIfAbsent(userID, k -> new TreeSet<>()).add(ins);
                    allShapes.add(ins);

                } catch (InvalidDimensionException e) {
                    System.out.println(e.getMessage());
                    break;
                } catch (InvalidIDException e) {
                    System.out.println(e.getMessage());
                }

            }
        }

        void scaleShapes (String userID, double coefficient) {
            if (userMap.containsKey(userID))
                userMap.get(userID).forEach(s -> s.scale(coefficient));
        }

        void printAllShapes () {
            allShapes.forEach(pw::println);
            pw.flush();
        }

        private double sumOfAreasPerUser(String user) {
            return userMap.get(user).stream().mapToDouble(Shape::area).sum();
        }

        void printByUserId() {
            userMap.entrySet().stream().sorted((a, b) -> {
                int i = Integer.compare(b.getValue().size(), a.getValue().size());
                if (i != 0) return i;
                return Double.compare(sumOfAreasPerUser(a.getKey()), sumOfAreasPerUser(b.getKey()));
                    })
                    .forEach(e -> {
                System.out.printf("Shapes of user: %s\n", e.getKey());

                e.getValue().stream().sorted(Comparator.comparing(Shape::perimeter))
                        .forEach(System.out::println);
            });
        }

        void statistics() {
            System.out.printf("count: %d\n", allShapes.size());
            System.out.printf("sum: %.2f\n", allShapes.stream().mapToDouble(Shape::area).sum());
            System.out.printf("min: %.2f\n", allShapes.stream().mapToDouble(Shape::area).min().orElse(.0));
            System.out.printf("average: %.2f\n", allShapes.stream().mapToDouble(Shape::area).average().orElse(.0));
            System.out.printf("max: %.2f\n", allShapes.stream().mapToDouble(Shape::area).max().orElse(.0));
        }
    }
}