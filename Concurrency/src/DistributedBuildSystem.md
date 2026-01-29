# Distributed Build System – Concurrency Exam Problem

## Context

You are implementing a **Distributed Build System** similar to a simplified CI/CD server.

Developers submit **build tasks** (compile, test, package). The system has:

* A **limited number of build workers**
* A **limited artifact storage capacity**
* **Build steps** that must execute in order
* Some builds produce **results**, others only side effects

The system must coordinate **multiple concurrent clients**, enforce **resource limits**, and handle **partial failures** correctly.

This problem is intentionally designed so that **incorrect synchronization will appear to work under light load but fail under contention**.

---

## Entities

### 1. BuildTask

Represents a build job submitted by a client.

* Each task has:

    * an ID
    * a list of steps
    * a flag indicating whether it produces a result

### 2. BuildWorker

Represents an execution slot in the system.

* Only a fixed number of workers may run at once
* Workers execute tasks **step-by-step**

### 3. ArtifactStorage

Stores build artifacts.

* Has **limited capacity**
* If full, builds that want to store artifacts must **wait**

### 4. BuildScheduler

The central coordinator.

* Accepts build submissions
* Assigns builds to workers
* Controls storage access
* Supports shutdown

---

## Functional Requirements

### 1. Worker Capacity Control

* At most **N builds** may execute concurrently
* Extra builds must wait
* No busy waiting allowed

### 2. Artifact Storage Constraint

* Storage has limited slots
* If storage is full:

    * builds that need storage must block
    * builds that do not produce artifacts may continue

### 3. Build Step Ordering

* Each build consists of multiple steps
* Steps must execute **in order**
* A failure in one step aborts the build

### 4. Result Handling

* Builds may optionally return a result
* Clients must be able to retrieve results asynchronously

### 5. Submission Semantics

* Submission may block if the system is overloaded
* Submissions after shutdown must fail immediately

### 6. Shutdown Semantics

* No new builds accepted after shutdown
* Already submitted builds must finish
* Threads blocked on submission or storage must be released safely

---

## What Is Being Tested

* Correct use of blocking synchronization
* Coordination across **multiple shared resources**
* Proper use of Runnable / Callable / Future
* Failure safety
* Shutdown correctness

---

## Starter Code (Java)

```java

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

    public String getName() {
      return name;
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
  }

  // ------------------------------------------------------
  // Scheduler
  // ------------------------------------------------------


  static class BuildScheduler {
    private final int maxWorkers;
    private final int artifactCapacity;

    public BuildScheduler(int maxWorkers, int artifactCapacity) {
      this.maxWorkers = maxWorkers;
      this.artifactCapacity = artifactCapacity;
    }

    /**
     * Submits a build that does not return a result.
     * This method may block if the system is overloaded.
     */
    public void submit(BuildTask<Void> task) {
      // TODO
    }

    /**
     * Submits a build that produces a result.
     * This method may block.
     */
    public <T> Future<T> submitWithResult(BuildTask<T> task) {
      // TODO
      return null;
    }

    /**
     * Called internally when a build finishes.
     * Must release all held resources.
     */
    private void onBuildFinished(BuildTask<?> task) {
      // TODO
    }

    /**
     * Initiates an orderly shutdown.
     */
    public void shutdown() {
      // TODO
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
```

---

## Notes

* Incorrect ordering or missing wakeups **will deadlock**
* Releasing resources in the wrong order **will starve builds**
* Improper shutdown handling **will hang threads**

This problem is designed so that a *naive solution fails under contention*.

---

Good luck.
