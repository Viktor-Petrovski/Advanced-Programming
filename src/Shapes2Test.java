import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public class Shapes2Test {
    static class IrregularCanvasException extends Exception {
        public IrregularCanvasException(String id, double area) {
            super(String.format("Canvas %s has a shape with area larger than %.2f", id, area));
        }
    }

    static class Canvas {
        private final String id;
        private int squares;
        private int circles;
        private double minArea = Double.MAX_VALUE;
        private double maxArea = Double.MIN_VALUE;
        private double sumAreas;

        Canvas(String l, double maxAllowedArea) throws IrregularCanvasException {
            // canvas_id type_1 size_1 type_2 size_2 type_3 size_3 …. type_n size_n
            String[] tokens =  l.split("\\s+");
            id = tokens[0];
            for (int i = 1; i < tokens.length; i++) {
                char shape = tokens[i].charAt(0); // S C
                int x = Integer.parseInt(tokens[++i]);
                double area = x * x;

                if (shape == 'C') {
                    area *= Math.PI;
                    circles++;
                }
                else squares++;

                minArea = Math.min(minArea, area);
                maxArea = Math.max(maxArea, area);
                sumAreas += area;

                if (area > maxAllowedArea)
                    throw new IrregularCanvasException(id, maxAllowedArea);
            }

        }

        double getSum() {
            return sumAreas;
        }

        @Override
        public String toString() {
            // ID total_shapes total_circles total_squares min_area max_area average_area
            int total = squares + circles;
            double avg = sumAreas / total;
            return String.format("%s %d %d %d %.2f %.2f %.2f",
                    id, total, circles, squares, minArea, maxArea, avg);
        }
    }

    static class ShapesApplication {
        private final double maxArea;
        private final List<Canvas> canvasList;

        ShapesApplication(double maxArea) {
            this.maxArea = maxArea;
            canvasList = new ArrayList<>();
        }

        void readCanvases() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().forEach(l -> {
                try {
                    Canvas ins = new Canvas(l, maxArea);
                    canvasList.add(ins);
                } catch (IrregularCanvasException e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        void printCanvases() {
            PrintWriter pw = new PrintWriter(System.out);
            canvasList.stream().sorted(Comparator.comparing(Canvas::getSum).reversed())
                    .forEach(pw::println);
            pw.flush();
        }
    }
    public static void main(String[] args) {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases();

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases();


    }
}