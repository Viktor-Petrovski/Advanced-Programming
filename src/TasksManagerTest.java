import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;

public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks();
        System.out.println("By categories with priority");
        manager.printTasks(true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(true, false);
        System.out.println("-------------------------");

    }

    static class DeadlineNotValidException extends Exception {
        public DeadlineNotValidException(LocalDateTime ldt) {
            super(String.format("The deadline %s has already passed", ldt));
        }
    }

    static class Task {
        private final String name;
        private final String description;
        private LocalDateTime deadline;
        private Integer priority;

        public Task(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public void setDeadline(LocalDateTime deadline) {
            this.deadline = deadline;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        @Override
        public String toString() {
            String deadline = this.deadline == null ? "" : ", deadline=" + this.deadline;
            String priority = this.priority == null ? "" : ", priority=" + this.priority;

            return "Task{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    deadline +
                    priority +
                    '}';
        }

        public static final Comparator<Task> BY_PRIORITY =
                Comparator.comparing(t -> t.priority, Comparator.nullsLast(Comparator.naturalOrder()));

        public static final Comparator<Task> BY_DATE =
                Comparator.comparing(t -> t.deadline, Comparator.nullsLast(Comparator.naturalOrder()));

    }

    static class TaskFactory {
        private static final int LDT_LEN = 23;
        public static final LocalDateTime CURRENT_DATE = LocalDateTime.parse("2020-06-02T00:00:00");

        // [категорија][име_на_задача],[oпис],[рок_за_задачата],[приоритет]
        static Task create(String s) throws DeadlineNotValidException {
            String[] tokens = s.split(",");
            Task task = new Task(tokens[1], tokens[2]);

            int amt = tokens.length;
            if (amt == 4 && tokens[3].length() == LDT_LEN) {
                LocalDateTime ldt = LocalDateTime.parse(tokens[3]);
                if (ldt.isBefore(CURRENT_DATE))
                    throw new DeadlineNotValidException(ldt);
                task.setDeadline(ldt);
            }
            else if (amt == 4)
                task.setPriority(Integer.parseInt(tokens[3]));
            else if (amt == 5) {
                LocalDateTime ldt = LocalDateTime.parse(tokens[3]);
                if (ldt.isBefore(CURRENT_DATE))
                    throw new DeadlineNotValidException(ldt);
                task.setDeadline(ldt);
                task.setPriority(Integer.parseInt(tokens[4]));
            }
            return task;
        }
    }

    static class TaskManager {
        private final Map<String, Collection<Task>> taskMap; // CATEGORY -> Tasks

        TaskManager() {
            taskMap = new HashMap<>();
        }

        void readTasks() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().forEach(l -> {
                try {
                    Task ins = TaskFactory.create(l);

                    String key = l.split(",")[0].toUpperCase();
                    taskMap.computeIfAbsent(key, k -> new ArrayList<>()).add(ins);
                } catch (DeadlineNotValidException e) {
                    System.out.println(e.getMessage());
                }
            });

        }

        void printTasks(boolean includePriority, boolean includeCategory) {
            Comparator<Task> CMP = includePriority ? Task.BY_PRIORITY.thenComparing(Task.BY_DATE) : Task.BY_DATE;
            if (includeCategory)
                taskMap.forEach((k, v) -> {
                    System.out.println(k);
                    v.stream().sorted(CMP).forEach(System.out::println);
                });

            else taskMap.values().stream().flatMap(Collection::stream)
                    .sorted(CMP).forEach(System.out::println);
        }
    }
}
