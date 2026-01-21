import java.util.*;
import java.util.stream.Collectors;

public class BlockContainerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int size = scanner.nextInt();
        BlockContainer<Integer> integerBC = new BlockContainer<>(size);
        scanner.nextLine();
        Integer lastInteger = null;
        for(int i = 0; i < n; ++i) {
            int element = scanner.nextInt();
            lastInteger = element;
            integerBC.add(element);
        }
        System.out.println("+++++ Integer Block Container +++++");
        System.out.println(integerBC);
        System.out.println("+++++ Removing element +++++");
        integerBC.remove(lastInteger);
        System.out.println("+++++ Sorting container +++++");
        integerBC.sort();
        System.out.println(integerBC);
        BlockContainer<String> stringBC = new BlockContainer<>(size);
        String lastString = null;
        for(int i = 0; i < n; ++i) {
            String element = scanner.next();
            lastString = element;
            stringBC.add(element);
        }
        System.out.println("+++++ String Block Container +++++");
        System.out.println(stringBC);
        System.out.println("+++++ Removing element +++++");
        stringBC.remove(lastString);
        System.out.println("+++++ Sorting container +++++");
        stringBC.sort();
        System.out.println(stringBC);
    }

    static class Block<T extends Comparable<T>> {
        private final int max;
        private final Set<T> set;

        Block(int max) {
            this.max = max;
            set = new TreeSet<>();
        }

        boolean add(T a) {
            if (set.size() >= max) return false; // full
            set.add(a);
            return true; // added
        }

        boolean rm(T a) {
            return set.remove(a) && !set.isEmpty();
        }

        public Set<T> getList() {
            return set;
        }

        @Override
        public String toString() {
            //[7, 8, 9]
            return set.stream().map(T::toString)
                    .collect(Collectors.joining(", ", "[", "]"));
        }
    }

    static class BlockContainer<T extends Comparable<T>>{
        private final int size;
        private List<Block<T>> blocks;

        BlockContainer(int size) {
            this.size = size;
            init();
        }

        private void init() {
            blocks = new ArrayList<>();
            blocks.add(new Block<>(size));
        }

        private Block<T> last() {
            return blocks.get(blocks.size() - 1);
        }

        public void add(T a) {
            if (! last().add(a)) {
                blocks.add(new Block<>(size));
                last().add(a);
            }
        }

        public void remove(T a) {
            if (! last().rm(a))
                blocks.remove(last());
        }

        public void sort() {
//            blocks.forEach(Block::sort); naive approach

            List<Block<T>> copy = new ArrayList<>(blocks);
            init();

            copy.stream()
                    .flatMap(b -> b.getList().stream())
                    .sorted()
                    .forEach(this::add);
        }

        @Override
        public String toString() {
            return blocks.stream().map(Block::toString).collect(Collectors.joining(","));
        }
    }
}
