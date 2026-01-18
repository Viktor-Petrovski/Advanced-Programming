import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Shapes1Test {

    static class Canvas {
        private final String id;
        private final List<Integer> squares;

        Canvas(String l) {
            squares = new ArrayList<>();
            String[] tokens = l.split("\\s+");

            id = tokens[0];
            Arrays.stream(tokens).skip(1).mapToInt(Integer::parseInt).forEach(squares::add);
        }

        int perimeterSums() {
            return squares.stream().mapToInt(i -> i*4).sum();
        }

        int getSize() {
            return squares.size();
        }

        @Override
        public String toString() {
            // canvas_id squares_count total_squares_perimeter
            return String.format("%s %d %d", id, squares.size(), perimeterSums());
        }
    }

    static class ShapesApplication {
        private final List<Canvas> canvasList;

        ShapesApplication() {
            canvasList = new ArrayList<>();
        }

        int readCanvases () {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().forEach(l -> canvasList.add(new Canvas(l)));
            return canvasList.stream().mapToInt(Canvas::getSize).sum();
        }

        void printLargestCanvasTo () {
            PrintWriter pw = new PrintWriter(System.out);
            canvasList.stream().max(Comparator.comparing(Canvas::perimeterSums)).ifPresent(pw::println);
            pw.flush();
        }
    }

    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases());
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo();

    }
}