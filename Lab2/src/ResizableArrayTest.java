import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ResizableArrayTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int test = jin.nextInt();
        if (test == 0) { //test ResizableArray on ints
            ResizableArray<Integer> a = new ResizableArray<>();
            System.out.println(a.count());
            int first = jin.nextInt();
            a.addElement(first);
            System.out.println(a.count());
            int last = first;
            while (jin.hasNextInt()) {
                last = jin.nextInt();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
        }
        if (test == 1) { //test ResizableArray on strings
            ResizableArray<String> a = new ResizableArray<>();
            System.out.println(a.count());
            String first = jin.next();
            a.addElement(first);
            System.out.println(a.count());
            String last = first;
            for (int i = 0; i < 4; ++i) {
                last = jin.next();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
            ResizableArray<String> b = new ResizableArray<>();
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));

            System.out.println(a.removeElement(first));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
        }
        if (test == 2) { //test IntegerArray
            IntegerArray a = new IntegerArray();
            System.out.println(a.isEmpty());
            while (jin.hasNextInt()) {
                a.addElement(jin.nextInt());
            }
            jin.next();
            System.out.println(a.sum());
            System.out.println(a.mean());
            System.out.println(a.countNonZero());
            System.out.println(a.count());
            IntegerArray b = a.distinct();
            System.out.println(b.sum());
            IntegerArray c = a.increment(5);
            System.out.println(c.sum());
            if (a.sum() > 100)
                ResizableArray.copyAll(a, a);
            else
                ResizableArray.copyAll(a, b);
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.contains(jin.nextInt()));
            System.out.println(a.contains(jin.nextInt()));
        }
        if (test == 3) { //test insanely large arrays
            LinkedList<ResizableArray<Integer>> resizable_arrays = new LinkedList<>();
            for (int w = 0; w < 500; ++w) {
                ResizableArray<Integer> a = new ResizableArray<>();
                int k = 2000;
                int t = 1000;
                for (int i = 0; i < k; ++i) {
                    a.addElement(i);
                }

                a.removeElement(0);
                for (int i = 0; i < t; ++i) {
                    a.removeElement(k - i - 1);
                }
                resizable_arrays.add(a);
            }
            System.out.println("You implementation finished in less then 3 seconds, well done!");
        }
    }

    static class ResizableArray<T> {
        protected T[] array;
        private int capacity;
        protected int size;

        private static final int DEFAULT_CAPACITY = 10;

        @SuppressWarnings("unchecked")
        ResizableArray() {
            this.capacity = DEFAULT_CAPACITY;
            this.size = 0;
            this.array = (T[]) new Object[capacity];
        }

        @SuppressWarnings("unchecked")
        private void resize(int newCapacity) {
            T[] resized = (T[]) new Object[newCapacity];
            System.arraycopy(array, 0, resized, 0, size);
            array = resized;
            capacity = newCapacity;
        }

        void addElement(T element) {
            if (size == capacity)
                resize((int) (capacity * 1.25));
            array[size++] = element;
        }

        boolean removeElement(T element) {
            for (int i = 0; i < size; i++) {
                if (elementAt(i).equals(element)) {

                    for (int j = i; j < size - 1; j++)
                        array[j] = array[j + 1];

                    array[--size] = null;

                    if (size > 0 && size <= capacity / 3 && capacity / 2> DEFAULT_CAPACITY)
                        resize(capacity / 2);

                    return true;
                }
            }
            return false;
        }

        boolean contains(T element) {
            for (int i = 0; i < size; i++) {
                if (elementAt(i).equals(element)) {
                    return true;
                }
            }
            return false;
        }

        Object[] toArray() {
            return Arrays.copyOf(array, size);
        }

        boolean isEmpty() {
            return size == 0;
        }

        int count() {
            return size;
        }

        T elementAt(int index) {
            if (index < 0 || index >= size) {
                throw new ArrayIndexOutOfBoundsException();
            }
            return array[index];
        }

        static <T> void copyAll(ResizableArray<? super T> dest, ResizableArray<? extends T> src) {
            for (int i = 0; i < src.size; i++) {
                dest.addElement(src.elementAt(i));
            }
        }
    }

    static class IntegerArray extends ResizableArray<Integer> {
        double sum() {
            int c = 0;
            for (int i = 0; i < size; i++) {
                c += elementAt(i);
            }
            return c;
        }

        double mean() {
            return sum() / size;
        }

        int countNonZero() {
            int c = 0;
            for (int i = 0; i < size; i++) {
                if (elementAt(i) != 0)
                    c++;
            }
            return c;
        }

        IntegerArray distinct() {
            IntegerArray res = new IntegerArray();
            Set<Integer> set = new HashSet<>();

            for (int i = 0; i < size; i++) {
                int el = elementAt(i);
                if (!set.contains(el)) {
                    res.addElement(el);
                    set.add(el);
                }
            }

            return res;
        }

        IntegerArray increment(int offset) {
            IntegerArray res = new IntegerArray();

            for (int i = 0; i < size; i++) {
                res.addElement(elementAt(i) + offset);
            }

            return res;
        }
    
    }

    static class ArrayTransformer {
        static <T, R> ResizableArray<R> map(ResizableArray<? extends T> source, Function<? super T, ? extends R> mapper) {
            ResizableArray<R> res = new ResizableArray<>();
            for (int i = 0; i < source.count(); i++) {
                res.addElement(mapper.apply(source.elementAt(i)));
            }
            return res;
        }

        static <T> ResizableArray<T> filter(ResizableArray<? extends T> source, Predicate<? super T> predicate) {
            ResizableArray<T> res = new ResizableArray<>();
            for (int i = 0; i < source.count(); i++) {
                if (predicate.test(source.elementAt(i)))
                    res.addElement(source.elementAt(i));
            }
            return res;
        }
    }

}
