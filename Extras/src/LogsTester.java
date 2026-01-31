import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LogsTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LogCollector collector = new LogCollector();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("addLog")) {
                collector.addLog(line.replace("addLog ", ""));
            } else if (line.startsWith("printServicesBySeverity")) {
                collector.printServicesBySeverity();
            } else if (line.startsWith("getSeverityDistribution")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                if (parts.length == 3) {
                    microservice = parts[2];
                }
                collector.getSeverityDistribution(service, microservice)
                        .forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
            } else if (line.startsWith("displayLogs")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                String order;
                if (parts.length == 4) {
                    microservice = parts[2];
                    order = parts[3];
                } else {
                    order = parts[2];
                }
                System.out.println(line);

                collector.displayLogs(service, microservice, order);
            }
        }
    }

    interface IOrderStrategy {
        Comparator<Log> getComparator();
    }

    private static final IOrderStrategy NEWEST_FIRST = () -> Comparator.comparing(Log::getTimestamp).reversed();
    private static final IOrderStrategy OLDEST_FIRST = () -> Comparator.comparing(Log::getTimestamp);
    private static final IOrderStrategy LEAST_SEVERE_FIRST = () -> Comparator.comparing(Log::getSeverity);
    private static final IOrderStrategy MOST_SEVERE_FIRST = () -> Comparator.comparing(Log::getSeverity).reversed();

    private static IOrderStrategy getStrategy(String o) {
        if ("NEWEST_FIRST".equals(o)) return NEWEST_FIRST;
        if ("OLDEST_FIRST".equals(o)) return OLDEST_FIRST;
        if ("LEAST_SEVERE_FIRST".equals(o)) return LEAST_SEVERE_FIRST;
        if ("MOST_SEVERE_FIRST".equals(o)) return MOST_SEVERE_FIRST;

        throw new IllegalArgumentException("Invalid order strategy: " + o);
    }

    abstract static class Log {
        protected String serviceName;
        protected String microserviceName;
        protected String message;
        protected long timestamp;

        public Log(String serviceName, String microserviceName, String message, long timestamp) {
            this.serviceName = serviceName;
            this.microserviceName = microserviceName;
            this.message = message;
            this.timestamp = timestamp;
        }

        public abstract int getSeverity();

        public String getServiceName() { return serviceName; }
        public String getMicroserviceName() { return microserviceName; }
        public long getTimestamp() { return timestamp; }

        public static final Comparator<Log> BY_TIMESTAMP_DESC = Comparator.comparing(Log::getTimestamp).reversed();
    }

    static class InfoLog extends Log {
        public InfoLog(String service, String micro, String message, long timestamp) {
            super(service, micro, message, timestamp);
        }

        @Override
        public int getSeverity() {
            return 0;
        }

        @Override
        public String toString() {
            return String.format("%s|%s [%s] %s %d T:%d",
                    serviceName, microserviceName, "INFO", message, timestamp, timestamp);
        }

    }

    static class WarnLog extends Log {
        public WarnLog(String service, String micro, String message, long timestamp) {
            super(service, micro, message, timestamp);
        }

        @Override
        public int getSeverity() {
            return 1 + (message.contains("might cause error") ? 1 : 0);
        }

        @Override
        public String toString() {
            return String.format("%s|%s [%s] %s %d T:%d",
                    serviceName, microserviceName, "WARN", message, timestamp, timestamp);
        }
    }

    static class ErrorLog extends Log {
        public ErrorLog(String service, String micro, String message, long timestamp) {
            super(service, micro, message, timestamp);
        }

        @Override
        public int getSeverity() {
            int severity = 3;
            if (message.contains("fatal")) severity += 2;
            if (message.contains("exception")) severity += 3;
            return severity;
        }

        @Override
        public String toString() {
            return String.format("%s|%s [%s] %s %d T:%d",
                    serviceName, microserviceName, "ERROR", message, timestamp, timestamp);
        }

    }

    static class LogCollector {
        private final List<Log> logList;

        LogCollector() {
            logList = new ArrayList<>();
        }

        void addLog(String s) {
            String[] tokens = s.split(" ");
            String service = tokens[0];
            String micro = tokens[1];
            String type = tokens[2];
            String message = Arrays.stream(tokens, 3, tokens.length - 1)
                    .collect(Collectors.joining(" "));
            long timestamp = Long.parseLong(tokens[tokens.length - 1]);

            Log log;
            switch (type) {
                case "INFO": log = new InfoLog(service, micro, message, timestamp); break;
                case "WARN": log = new WarnLog(service, micro, message, timestamp); break;
                case "ERROR": log = new ErrorLog(service, micro, message, timestamp); break;
                default: throw new IllegalArgumentException("Unknown log type: " + type);
            }

            logList.add(log);
        }

        private double averageSeverity(Collection<Log> logs) {
            return logs.stream().mapToInt(Log::getSeverity).average().orElse(.0);
        }

        private Map<String, List<Log>> groupByService() {
            Comparator<Map.Entry<String, List<Log>>> comparator =
                    Comparator.comparingDouble(e -> averageSeverity(e.getValue()));

            return logList.stream()
                    .collect(Collectors.groupingBy(Log::getServiceName))
                    .entrySet().stream()
                    .sorted(comparator.reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> a,
                            LinkedHashMap::new
                    ));
        }


        /// метод кој ќе ги испечати сите сервиси за кои колекторот има собрано логови сортирани според просечната сериозност
        /// на сите логовите произведени од тој сервис во опаѓачки редослед.
        void printServicesBySeverity() {
            Map<String, List<Log>> byService = groupByService();
            byService.forEach((k, v) -> {
                long amountMicroservices = v.stream().map(Log::getMicroserviceName).distinct().count();

                System.out.printf("Service name: %s Count of microservices: %d ", k, amountMicroservices);
                System.out.printf("Total logs in service: %d Average severity for all logs: %.2f ", v.size(), averageSeverity(v));
                System.out.printf("Average number of logs per microservice: %.2f\n", v.size() / (1.0 * amountMicroservices));
            });
            //Service name: service2 Count of microservices: 3 Total logs in service: 5 Average severity for all logs: 3.40 Average number of logs per microservice: 1.67↩
        }

        Map<Integer, Integer> getSeverityDistribution(String service, String microservice) {
            Predicate<Log> predicate = l -> l.getServiceName().equals(service) &&
                    (microservice == null || l.getMicroserviceName().equals(microservice));

            return logList.stream().filter(predicate).collect(Collectors.groupingBy(
                    Log::getSeverity,
                    TreeMap::new,
                    Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
        }

        void displayLogs(String service, String microservice, String order) {
            Comparator<Log> comparator = getStrategy(order).getComparator();
            Predicate<Log> predicate = l -> l.getServiceName().equals(service) &&
                    l.getMicroserviceName().equals(microservice);

            logList.stream().filter(predicate).sorted(comparator.thenComparing(Log.BY_TIMESTAMP_DESC))
                    .forEach(System.out::println);
        }
    }
}