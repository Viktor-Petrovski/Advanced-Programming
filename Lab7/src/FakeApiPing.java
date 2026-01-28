import java.util.*;
import java.util.concurrent.*;

public class FakeApiPing {

    // Result holder
    public static class ApiResult {
        public final int requestId;
        public final boolean success;
        public final String value;

        public ApiResult(int requestId, boolean success, String value) {
            this.requestId = requestId;
            this.success = success;
            this.value = value;
        }

        @Override
        public String toString() {
            return "ApiResult{" +
                    "requestId=" + requestId +
                    ", success=" + success +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class Api {
        public static ApiResult get(int requestId, int parameter) throws InterruptedException {
            long delayMillis = parameter * 100L;
            Thread.sleep(delayMillis);

            String response = "VALUE_" + parameter;
            return new ApiResult(requestId, true, response);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt(); // number of API calls

        List<Callable<ApiResult>> tasks = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int parameter = sc.nextInt();
            int requestId = i + 1;

            tasks.add(() -> Api.get(requestId, parameter));
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Submit all callables
        List<Future<ApiResult>> futures = new ArrayList<>();
        tasks.forEach(t -> futures.add(executor.submit(t)));

        long timeoutMillis = 200;

        List<ApiResult> results = new ArrayList<>();

        // Get results with timeout handling
        for (int i = 0; i < futures.size(); i++) {
            Future<ApiResult> f = futures.get(i);
            int requestId = i + 1;

            try {
                results.add(f.get(timeoutMillis, TimeUnit.MILLISECONDS));
            } catch (TimeoutException e) {
                f.cancel(true); // !important
                results.add(new ApiResult(requestId, false, "TIMEOUT"));
            } catch (ExecutionException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

//         Потребно е да се имплементира механизам кој ќе го ограничи бројот на API повици што се извршуваат паралелно.
//         По детекција на првиот API повик кој не завршил во дозволеното време,
//         сите останати активни и неисполнети повици ќе бидат прекинати
//        API повиците кои се прекинати мора да бидат означени како неуспешни (FAILED)
//
//        boolean errorFlag = false;
//        for (int i = 0; i < futures.size(); i++) {
//            Future<ApiResult> f = futures.get(i);
//            int requestId = i + 1;
//
//            if (errorFlag) {
//                results.add(new ApiResult(requestId, false, "FAILED"));
//                continue;
//            }
//
//            try {
//                results.add(f.get(timeoutMillis, TimeUnit.MILLISECONDS));
//            } catch (ExecutionException | InterruptedException e) {
//                throw new RuntimeException(e);
//            } catch (TimeoutException e) {
//                f.cancel(true);
//                results.add(new ApiResult(requestId, false, "TIMEOUT"));
//                errorFlag = true;
//            }
//
//        }

        // for this additional requirement I implemented a flag that is set to true once one future times out
        // then the rest are marked as failed

        executor.shutdown();

        // Sorting by requestId
        results.sort(Comparator.comparingInt(r -> r.requestId));

        // Output
        for (ApiResult r : results) {
            System.out.printf(
                    "%d %s %s%n",
                    r.requestId,
                    r.success ? "OK" : "FAILED",
                    r.value
            );
        }
    }
}
