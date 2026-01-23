import java.util.*;

public class DeliveryAppTester {

    interface Location {
        int getX();

        int getY();

        default int distance(Location other) {
            int xDiff = Math.abs(getX() - other.getX());
            int yDiff = Math.abs(getY() - other.getY());
            return xDiff + yDiff;
        }
    }

    static class LocationCreator {
        public static Location create(int x, int y) {

            return new Location() {
                @Override
                public int getX() {
                    return x;
                }

                @Override
                public int getY() {
                    return y;
                }
            };
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }

    static class DeliveryPerson implements Comparable<DeliveryPerson>{
        private final String id;
        private final String name;
        private Location currentLocation;

        private final List<Integer> totalProfit;

        DeliveryPerson(String id, String name, Location currentLocation) {
            this.id = id;
            this.name = name;
            this.currentLocation = currentLocation;

            totalProfit = new ArrayList<>();
        }

        public Location getLocation() {
            return currentLocation;
        }

        public void setCurrentLocation(Location currentLocation) {
            this.currentLocation = currentLocation;
        }

        void addProfit(int profit) {
            totalProfit.add(profit);
        }

        private int totalProfit() {
            return totalProfit.stream().mapToInt(i -> i).sum();
        }

        private int deliveriesMade() {
            return totalProfit.size();
        }

        //ID: 2 Name: Riste Total deliveries: 1 Total delivery fee: 90.00 Average delivery fee: 90.00
        @Override
        public String toString() {
            int totalOrders = deliveriesMade();
            double totalAmount = totalProfit();
            double averageAmount = totalProfit.stream().mapToDouble(p -> p).average().orElse(.0);

            return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f",
                    id, name, totalOrders, totalAmount, averageAmount);
        }


        public static final Comparator<DeliveryPerson> BY_DELIVERIES_MADE = Comparator.comparing(DeliveryPerson::deliveriesMade);

        @Override
        public int compareTo(DeliveryPerson o) {
            int i = Integer.compare(o.totalProfit(), this.totalProfit());
            if (i != 0) return i;
            i = o.id.compareTo(id);
            return i != 0 ? i : 1;
        }
    }

    static class Restaurant implements Comparable<Restaurant>{
        private final String id;
        private final String name;
        private final Location location;

        private final List<Float> profit;

        Restaurant(String id, String name, Location location) {
            this.id = id;
            this.name = name;
            this.location = location;

            profit = new ArrayList<>();
        }

        public Location getLocation() {
            return location;
        }

        void buyItem(float cost) {
            profit.add(cost);
        }

        private float avg() {
            return (float) profit.stream().mapToDouble(i -> i).average().orElse(.0);
        }

        @Override
        public String toString() {
            // ID: 4 Name: restaurant4 Total orders: 2 Total amount earned: 2332.00 Average amount earned: 1166.00
            int totalOrders = profit.size();
            double totalAmount = profit.stream().mapToDouble(p -> p).sum();
            double averageAmount = profit.stream().mapToDouble(p -> p).average().orElse(.0);

            return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f",
                    id, name, totalOrders, totalAmount, averageAmount);
        }

        @Override
        public int compareTo(Restaurant o) {
            int i = Double.compare(o.avg(), this.avg());
            if (i != 0) return i;
            i = o.id.compareTo(id);
            return i != 0 ? i : 1;
        }

    }

    static class User implements Comparable<User>{
        private final String id;
        private final String name;
        private final Map<String, Location> addressMap;

        private final List<Float> spent;

        User(String id, String name) {
            this.id = id;
            this.name = name;

            addressMap = new HashMap<>();
            spent = new ArrayList<>();
        }

        void addAddress(String addr, Location loc) {
            addressMap.put(addr, loc);
        }

        Location getAddr(String addr) {
            return addressMap.get(addr);
        }

        void spend(float amount) {
            spent.add(amount);
        }

        private float getSpent() {
            return (float) spent.stream().mapToDouble(i -> i).sum();
        }

        //ID: 2 Name: user2 Total orders: 1 Total amount spent: 1452.00 Average amount spent: 1452.00
        @Override
        public String toString() {
            int totalOrders = spent.size();
            double totalAmount = getSpent();
            double averageAmount = spent.stream().mapToDouble(p -> p).average().orElse(.0);

            return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",
                    id, name, totalOrders, totalAmount, averageAmount);
        }

        @Override
        public int compareTo(User o) {
            int i = Double.compare(o.getSpent(), this.getSpent());
            if (i != 0) return i;
            i = o.id.compareTo(id);
            return i != 0 ? i : 1;
        }

    }

    static class DeliveryApp {
        private final String name;
        private final List<DeliveryPerson> deliveryPersons; // id -> DP
        private final Map<String, Restaurant> restaurantMap; // id -> Restaurant
        private final Map<String, User> userMap; // id -> User

        DeliveryApp(String name) {
            this.name = name;

            deliveryPersons = new ArrayList<>();
            restaurantMap = new HashMap<>();
            userMap = new HashMap<>();
        }

        void registerDeliveryPerson (String id, String name, Location currentLocation) {
            deliveryPersons.add(new DeliveryPerson(id, name, currentLocation));
        }

        void addRestaurant (String id, String name, Location location) {
            restaurantMap.putIfAbsent(id, new Restaurant(id, name, location));
        }

        void addUser (String id, String name) {
            userMap.putIfAbsent(id, new User(id, name));
        }

        void addAddress (String id, String addressName, Location location) {
            userMap.get(id).addAddress(addressName, location);
        }

        private int minDistance(Restaurant restaurant) {
            return deliveryPersons.stream()
                    .mapToInt(dp -> restaurant.getLocation().distance(dp.getLocation()))
                    .min().orElse(Integer.MAX_VALUE);
        }

        private Optional<DeliveryPerson> findDP(Restaurant restaurant) {
            int minDistance = minDistance(restaurant);

            return deliveryPersons.stream()
                    .filter(dp -> restaurant.getLocation().distance(dp.getLocation()) == minDistance)
                    .min(DeliveryPerson.BY_DELIVERIES_MADE);
        }


        void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
            User user = userMap.get(userId);
            Location location = user.getAddr(userAddressName);
            Restaurant restaurant = restaurantMap.get(restaurantId);
            DeliveryPerson dp = findDP(restaurant).orElseThrow(NullPointerException::new);

            int distance = minDistance(restaurant);

            int profitDp = distance - (distance % 10) + 90;
            dp.addProfit(profitDp);
            restaurant.buyItem(cost);
            user.spend(cost);

            dp.setCurrentLocation(location);
        }

        /// метод кој ги печати сите корисници на апликацијата сортирани во опаѓачки редослед
        /// според потрошениот износ за нарачка на храна преку апликацијата
        void printUsers() {
            userMap.values().stream().sorted().forEach(System.out::println);
        }

        /// метод кој ги печати сите регистрирани ресторани во апликацијата, сортирани во опаѓачки редослед
        /// според просечната цена на нарачките наплатени преку апликацијата
        void printRestaurants() {
            restaurantMap.values().stream().sorted().forEach(System.out::println);
        }

        /// метод кој ги печати сите регистрирани доставувачи сортирани во опачки редослед според заработениот износ од извршените достави
        void printDeliveryPeople() {
            deliveryPersons.stream().sorted().forEach(System.out::println);
        }
    }
}
