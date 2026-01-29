import java.util.*;
import java.util.concurrent.*;

public class DistributedBuildSystem {

    // ------------------------------------------------------
    // Domain classes
    // ------------------------------------------------------

    static class BuildStep {
        private final String name;
        private final long durationMs;

        public BuildStep(String name, long durationMs) {
            this.name = name;
            this.durationMs = durationMs;
        }

        public void execute() throws InterruptedException {
            Thread.sleep(durationMs);
        }

        @Override
        public String toString() {
            return "BuildStep{" +
                    "name='" + name + '\'' +
                    ", durationMs=" + durationMs +
                    '}';
        }
    }

    static class BuildTask<T> {
        private final String id;
        private final List<BuildStep> steps;
        private final Callable<T> resultProducer; // may be null

        public BuildTask(String id, List<BuildStep> steps, Callable<T> resultProducer) {
            this.id = id;
            this.steps = steps;
            this.resultProducer = resultProducer;
        }

        public String getId() {
            return id;
        }

        public List<BuildStep> getSteps() {
            return steps;
        }

        public boolean producesResult() {
            return resultProducer != null;
        }

        public Callable<T> getResultProducer() {
            return resultProducer;
        }

        @Override
        public String toString() {
            return "BuildTask{" +
                    "id='" + id + '\'' +
                    ", steps=" + steps +
                    ", resultProducer=" + resultProducer +
                    '}';
        }
    }

    // ------------------------------------------------------
    // Scheduler
    // ------------------------------------------------------

    static class BuildScheduler {

        private final Semaphore workerPermits;
        private final Semaphore artifactPermits;
        private final ExecutorService executor;
        private volatile boolean shutdown = false;

        public BuildScheduler(int maxWorkers, int artifactCapacity) {
            this.workerPermits = new Semaphore(maxWorkers);
            this.artifactPermits = new Semaphore(artifactCapacity);
            this.executor = Executors.newCachedThreadPool();
        }


        /**
         * Submits a build that does not return a result.
         * This method may block if the system is overloaded.
         */
        public void submit(BuildTask<?> task) throws InterruptedException {
            if (shutdown) {
                throw new RejectedExecutionException("Scheduler is shut down");
            }

            workerPermits.acquire();

            executor.execute(() -> {
                try {
                    runBuild(task);
                } catch (Exception e) {
                    System.out.println("Build " + task.getId() + " failed");
                } finally {
                    onBuildFinished(task);
                }
            });


        }

        /**
         * Submits a build that produces a result.
         * This method may block.
         */
        public <T> Future<T> submitWithResult(BuildTask<T> task) throws InterruptedException {
            if (shutdown) {
                throw new RejectedExecutionException("Scheduler is shut down");
            }

            workerPermits.acquire();
            FutureTask<T> future = new FutureTask<>(() -> {
                try {
                    runBuild(task);
                    return task.getResultProducer().call();
                } finally {
                    onBuildFinished(task);
                }
            });

            executor.execute(future);
            return future;
        }

        /**
         * Executes build steps and handles artifact storage.
         */
        private void runBuild(BuildTask<?> task) throws Exception {
            for (BuildStep step : task.getSteps()) {
                step.execute();
            }

            if (task.producesResult()) {
                artifactPermits.acquire();
            }
        }

        /**
         * Called internally when a build finishes.
         * Must release all held resources.
         */
        private void onBuildFinished(BuildTask<?> task) {
            if (task.producesResult()) {
                artifactPermits.release();
            }
            workerPermits.release();
        }

        /**
         * Initiates an orderly shutdown.
         */
        public void shutdown() throws InterruptedException {
            shutdown = true;
            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS))
                executor.shutdownNow();
        }
    }

    // ------------------------------------------------------
    // Tester main
    // ------------------------------------------------------

    public static void main(String[] args) throws Exception {
        BuildScheduler scheduler = new BuildScheduler(2, 1);

        List<BuildStep> steps = List.of(
                new BuildStep("compile", 500),
                new BuildStep("test", 700),
                new BuildStep("package", 300)
        );

        for (int i = 1; i <= 4; i++) {
            int id = i;
            new Thread(() -> {
                try {
                    BuildTask<Integer> task = new BuildTask<>(
                            "build-" + id,
                            steps,
                            () -> id * 10
                    );

                    Future<Integer> result = scheduler.submitWithResult(task);
                    System.out.println("Result for build-" + id + " = " + result.get());
                } catch (Exception e) {
                    System.out.println("Build failed: " + e.getMessage());
                }
            }).start();
        }

        Thread.sleep(4000);
        scheduler.shutdown();
    }
}
