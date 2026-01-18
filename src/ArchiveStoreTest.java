
import java.nio.channels.NonReadableChannelException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while (scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch (NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }

    static class NonExistingItemException extends Exception {
        public NonExistingItemException(int id) {
            super(String.format("Item with id %d doesn't exist", id));
        }
    }

    static abstract class Archive {
        protected final int id;
        protected LocalDate dateArchived;

        Archive(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public LocalDate getDateArchived() {
            return dateArchived;
        }

        public void setDateArchived(LocalDate dateArchived) {
            this.dateArchived = dateArchived;
        }

        abstract String open(LocalDate date);
    }

    static class LockedArchive extends Archive {
        private final LocalDate dateToOpen;

        LockedArchive(int id, LocalDate dateToOpen) {
            super(id);
            this.dateToOpen = dateToOpen;
        }

        @Override
        String open(LocalDate date) {
            return date.isBefore(dateToOpen) ?
                    String.format("Item %d cannot be opened before %s", id, dateToOpen) :
                    String.format("Item %d opened at %s", id, date);
        }
    }

    static class SpecialArchive extends Archive {
        private final int maxOpen;
        private int timesOpened;

        SpecialArchive(int id, int maxOpen) {
            super(id);
            this.maxOpen = maxOpen;
            timesOpened = 0;
        }

        @Override
        String open(LocalDate date) {
            if (timesOpened >= maxOpen)
                return String.format("Item %d cannot be opened more than %d times", id, maxOpen);
            timesOpened++;
            return String.format("Item %d opened at %s", id, date);
        }
    }

    static class ArchiveStore {
        private final List<Archive> archiveList;
        private final StringBuilder log;

        ArchiveStore() {
            archiveList = new ArrayList<>();
            log = new StringBuilder();
        }

        private Archive findById(int id) throws NonExistingItemException {
            return archiveList.stream().filter(a -> a.getId() == id).findFirst().orElseThrow(() -> new NonExistingItemException(id));
        }

        void archiveItem(Archive item, LocalDate date) {
            item.setDateArchived(date);
            archiveList.add(item);
            log.append(String.format("Item %d archived at %s\n", item.getId(), date));
        }

        void openItem(int id, LocalDate date) throws NonExistingItemException {
            Archive opened = findById(id);
            log.append(opened.open(date)).append("\n");
        }

        String getLog() {
            return log.toString();
        }
    }
}