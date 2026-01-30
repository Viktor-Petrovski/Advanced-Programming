import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class QuizProcessorTest {
    public static void main(String[] args) {
        QuizProcessor.processAnswers().forEach((k, v) -> System.out.printf("%s -> %.2f%n", k, v));
    }

    static class UnbalancedAnswersException extends Exception {
        public UnbalancedAnswersException() {
            super("A quiz must have same number of correct and selected answers");
        }
    }

    static class QuizProcessor {

        static void answersValidator(String s) throws UnbalancedAnswersException {
            String[] tokens = s.split(";");
            String[] correct = tokens[1].split(", ");
            String[] student = tokens[2].split(", ");

            if (correct.length != student.length)
                throw new UnbalancedAnswersException();
        }
        /// ID; C1, C2, C3, C4, C5, … ,Cn; A1, A2, A3, A4, A5, …,An.
        /// каде што ID е индексот на студентот, Ci е точниот одговор на i-то прашање,
        /// а Ai е одговорот на студентот на i-то прашање. Студентот добива по 1 поен за точен одговор,
        /// а по -0.25 за секој неточен одговор. Бројот на прашања n може да биде различен во секој квиз.
        ///
        /// Со помош на исклучоци да се игнорира квиз во кој бројот на точни одговори е
        /// различен од бројот на одговорите на студентот.
        ///
        /// Во резултантната мапа, клучеви се индексите на студентите, а вредности се поените кои студентот ги освоил.
        /// Пример ако студентот на квиз со 6 прашања, има точни 3 прашања, а неточни 3 прашања, студентот
        /// ќе освои 3*1 - 3*0.25 = 2.25.
        static Map<String, Double> processAnswers() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            return br.lines().filter(s -> {
                    try {
                        answersValidator(s);
                        return true;
                    } catch (UnbalancedAnswersException e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                 }).collect(Collectors.toMap(
                    s -> s.split(";")[0],
                    s -> {
                        String[] tokens = s.split(";");
                        String[] correct = tokens[1].split(", ");
                        String[] student = tokens[2].split(", ");

                        double totalPoints = 0;
                        for (int i = 0; i < correct.length; i++)
                            totalPoints = correct[i].equals(student[i]) ? totalPoints + 1 : totalPoints - 0.25;
                        return totalPoints;
                    }, (x, y) -> x, TreeMap::new
            ));
        }
    }
}