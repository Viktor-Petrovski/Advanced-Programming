import java.util.*;
import java.util.stream.Collectors;

public class CarTest {
    public static void main(String[] args) {
        CarCollection carCollection = new CarCollection();
        String manufacturer = fillCollection(carCollection);
        carCollection.sortByPrice(true);
        System.out.println("=== Sorted By Price ASC ===");
        print(carCollection.getList());
        carCollection.sortByPrice(false);
        System.out.println("=== Sorted By Price DESC ===");
        print(carCollection.getList());
        System.out.printf("=== Filtered By Manufacturer: %s ===\n", manufacturer);
        List<Car> result = carCollection.filterByManufacturer(manufacturer);
        print(result);
    }

    static void print(List<Car> cars) {
        for (Car c : cars) {
            System.out.println(c);
        }
    }

    static String fillCollection(CarCollection cc) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            if(parts.length < 4) return parts[0];
            Car car = new Car(parts[0], parts[1], Integer.parseInt(parts[2]),
                    Float.parseFloat(parts[3]));
            cc.addCar(car);
        }
        scanner.close();
        return "";
    }

    static class Car {
        private final String manufacturer;
        private final String model;
        private final int price;
        private final float power;

        Car(String manufacturer, String model, int price, float power) {
            this.manufacturer = manufacturer;
            this.model = model;
            this.price = price;
            this.power = power;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        @Override
        public String toString() {
            // Fiat Punto (65KW) 13500
            return String.format("%s %s (%.0fKW) %d", manufacturer, model, power, price);
        }

        public static final Comparator<Car> BY_PRICE = Comparator.comparing(c -> c.price);
        public static final Comparator<Car> BY_POWER = Comparator.comparing(c -> c.power);
        public static final Comparator<Car> BY_MODEL = Comparator.comparing(c -> c.model);
    }

    static class CarCollection {
        private final List<Car> cars;

        CarCollection() {
            cars = new ArrayList<>();
        }

        public void addCar(Car car) {
            cars.add(car);
        }

        public void sortByPrice(boolean ascending) {
            Comparator<Car> C = Car.BY_PRICE.thenComparing(Car.BY_POWER);
            C = ascending ? C : C.reversed();
            cars.sort(C);
        }

        public List<Car> filterByManufacturer(String manufacturer) {
            return cars.stream()
                    .filter(c -> c.getManufacturer().equalsIgnoreCase(manufacturer))
                    .sorted(Car.BY_MODEL)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public List<Car> getList() {
            return cars;
        }
    }
}
