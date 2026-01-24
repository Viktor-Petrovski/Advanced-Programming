import java.util.*;
import java.util.function.Predicate;

public class CirclesTest {

    public static void main(String[] args) {

        System.out.println("===COLLECTION CONSTRUCTOR AND ADD METHOD TEST===");
        MovablesCollection collection = new MovablesCollection(100, 100);
        Scanner sc = new Scanner(System.in);
        int samples = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < samples; i++) {
            String inputLine = sc.nextLine();
            String[] parts = inputLine.split(" ");

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int xSpeed = Integer.parseInt(parts[3]);
            int ySpeed = Integer.parseInt(parts[4]);

            try {
                if (Integer.parseInt(parts[0]) == 0) { //point
                    collection.addMovableObject(new MovablePoint(x, y, xSpeed, ySpeed));
                } else { //circle
                    int radius = Integer.parseInt(parts[5]);
                    collection.addMovableObject(new MovableCircle(radius, new MovablePoint(x, y, xSpeed, ySpeed)));
                }
            } catch (MovableObjectNotFittableException e) {
                System.out.println(e.getMessage());
            }

        }
        System.out.println(collection);

        System.out.println("MOVE POINTS TO THE LEFT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.LEFT);
        System.out.println(collection);

        System.out.println("MOVE CIRCLES DOWN");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.DOWN);
        System.out.println(collection);

        System.out.println("CHANGE X_MAX AND Y_MAX");
        MovablesCollection.setX_MAX(90);
        MovablesCollection.setY_MAX(90);

        System.out.println("MOVE POINTS TO THE RIGHT");
        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.RIGHT);
        System.out.println(collection);

        System.out.println("MOVE CIRCLES UP");
        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.UP);
        System.out.println(collection);

    }

    enum TYPE {
        POINT,
        CIRCLE
    }

    enum DIRECTION {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    static class ObjectCanNotBeMovedException extends Exception {
        public ObjectCanNotBeMovedException(String message) {
            super(message);
        }
    }

    static class MovableObjectNotFittableException extends Exception {
        public MovableObjectNotFittableException(String message) {
            super(message);
        }
    }

    interface Movable {
        void moveUp() throws ObjectCanNotBeMovedException;

        void moveDown() throws ObjectCanNotBeMovedException;

        void moveRight() throws ObjectCanNotBeMovedException;

        void moveLeft() throws ObjectCanNotBeMovedException;

        int MIN = 0;

        boolean canFit();

        String fitError();
    }

    static class MovablePoint implements Movable {
        private int x;
        private int y;
        private final int xSpeed;
        private final int ySpeed;

        static private int xMax;
        static private int yMax;

        public MovablePoint(int x, int y, int xSpeed, int ySpeed) {
            this.x = x;
            this.y = y;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
        }

        public static void setX_Max(int xMax) {
            MovablePoint.xMax = xMax;
        }

        public static void setY_Max(int yMax) {
            MovablePoint.yMax = yMax;
        }

        @Override
        public void moveUp() throws ObjectCanNotBeMovedException {
            if (y + ySpeed > xMax)
                throw new ObjectCanNotBeMovedException(String.format("Point (%d,%d) is out of bounds", x, y + ySpeed));
            y += ySpeed;
        }

        @Override
        public void moveDown() throws ObjectCanNotBeMovedException {
            if (y - ySpeed < MIN)
                throw new ObjectCanNotBeMovedException(String.format("Point (%d,%d) is out of bounds", x, y - ySpeed));
            y -= ySpeed;
        }

        @Override
        public void moveRight() throws ObjectCanNotBeMovedException {
            if (x + xSpeed > xMax)
                throw new ObjectCanNotBeMovedException(String.format("Point (%d,%d) is out of bounds", x + xSpeed, y));
            x += xSpeed;
        }

        @Override
        public void moveLeft() throws ObjectCanNotBeMovedException {
            if (x - xSpeed < MIN)
                throw new ObjectCanNotBeMovedException(String.format("Point (%d,%d) is out of bounds", x - xSpeed, y));
            x -= xSpeed;
        }

        private int getCurrentXPosition() {
            return x;
        }

        private int getCurrentYPosition() {
            return y;
        }

        @Override
        public boolean canFit() {
            return x >= MIN && x <= xMax
                    && y >= MIN && y <= yMax;
        }

        @Override
        public String fitError() {
            return String.format("Movable point with coordinates (%d,%d) can not be fitted into the collection", x, y);
        }

        @Override
        public String toString() {
            return String.format("Movable point with coordinates (%d,%d)\n", x, y);
        }
    }

    static class MovableCircle implements Movable {
        private final int radius;
        private final MovablePoint center;

        public MovableCircle(int radius, MovablePoint center) {
            this.radius = radius;
            this.center = center;
        }


        @Override
        public void moveUp() throws ObjectCanNotBeMovedException {
            center.moveUp();
        }

        @Override
        public void moveDown() throws ObjectCanNotBeMovedException {
            center.moveDown();
        }

        @Override
        public void moveRight() throws ObjectCanNotBeMovedException {
            center.moveRight();
        }

        @Override
        public void moveLeft() throws ObjectCanNotBeMovedException {
            center.moveLeft();
        }

        @Override
        public boolean canFit() {
            int x = center.getCurrentXPosition();
            int y = center.getCurrentYPosition();

            return (x - radius) >= MovablePoint.MIN &&
                    (x + radius) <= MovablePoint.xMax &&
                    (y - radius) >= MovablePoint.MIN &&
                    (y + radius) <= MovablePoint.yMax;
        }

        @Override
        public String fitError() {
            return String.format("Movable circle with center (%d,%d) and radius %d can not be fitted into the collection",
                    center.getCurrentXPosition(), center.getCurrentYPosition(), radius);
        }

        // Movable circle with center coordinates (48,21) and radius 3
        @Override
        public String toString() {
            return String.format("Movable circle with center coordinates (%d,%d) and radius %d\n",
                    center.getCurrentXPosition(), center.getCurrentYPosition(), radius);
        }

    }

    static class MovablesCollection {

        private final List<Movable> movables;

        MovablesCollection(int x_MAX, int y_MAX) {
            movables = new ArrayList<>();
            MovablePoint.setX_Max(x_MAX);
            MovablePoint.setY_Max(y_MAX);
        }

        public static void setX_MAX(int x_MAX) {
            MovablePoint.setX_Max(x_MAX);
        }

        public static void setY_MAX(int y_MAX) {
            MovablePoint.setY_Max(y_MAX);
        }

        void addMovableObject(Movable m) throws MovableObjectNotFittableException {
            boolean val = m.canFit();
            if (!val)
                throw new MovableObjectNotFittableException(m.fitError());
            movables.add(m);
        }

        /// придвижување на движечките објекти од тип type во насока direction.
        /// TYPE и DIRECTION се енумерации кои се задедени во почетниот код.
        /// Во зависност од насоката зададена во аргументот,
        /// да се повика соодветниот метод за придвижување.
        void moveObjectsFromTypeWithDirection (TYPE type, DIRECTION direction) {
            Predicate<Movable> condition = m -> (type.equals(TYPE.POINT) && m instanceof MovablePoint) ||
                    (type.equals(TYPE.CIRCLE) && m instanceof MovableCircle);


            movables.stream().filter(condition).forEach(m -> {
                try{
                    if (direction.equals(DIRECTION.UP))
                        m.moveUp();
                    else if (direction.equals(DIRECTION.DOWN))
                        m.moveDown();
                    else if (direction.equals(DIRECTION.LEFT))
                        m.moveLeft();
                    else m.moveRight();
                } catch (ObjectCanNotBeMovedException e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Collection of movable objects with size %d:\n", movables.size()));

            movables.forEach(sb::append);

            return sb.toString();
        }
    }
}
