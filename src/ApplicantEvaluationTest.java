import java.util.Scanner;

public class ApplicantEvaluationTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        int creditScore = scanner.nextInt();
        int employmentYears = scanner.nextInt();
        boolean hasCriminalRecord = scanner.nextBoolean();
        int choice = scanner.nextInt();
        Applicant applicant = new Applicant(name, creditScore, employmentYears, hasCriminalRecord);
        Evaluator.TYPE type = Evaluator.TYPE.values()[choice];
        Evaluator evaluator;
        try {
            evaluator = EvaluatorBuilder.build(type);
            System.out.println("Applicant");
            System.out.println(applicant);
            System.out.println("Evaluation type: " + type.name());
            if (evaluator.evaluate(applicant)) {
                System.out.println("Applicant is ACCEPTED");
            } else {
                System.out.println("Applicant is REJECTED");
            }
        } catch (InvalidEvaluation invalidEvaluation) {
            System.out.println("Invalid evaluation");
        }
    }

    static class InvalidEvaluation extends Exception {

    }

    static class Applicant {
        private final String name;

        private final int creditScore;
        private final int employmentYears;
        private final boolean hasCriminalRecord;

        public Applicant(String name, int creditScore, int employmentYears, boolean hasCriminalRecord) {
            this.name = name;
            this.creditScore = creditScore;
            this.employmentYears = employmentYears;
            this.hasCriminalRecord = hasCriminalRecord;
        }

        public int getCreditScore() {
            return creditScore;
        }

        public int getEmploymentYears() {
            return employmentYears;
        }

        public boolean hasCriminalRecord() {
            return hasCriminalRecord;
        }

        @Override
        public String toString() {
            return String.format("Name: %s\nCredit score: %d\nExperience: %d\nCriminal record: %s\n",
                    name, creditScore, employmentYears, hasCriminalRecord ? "Yes" : "No");
        }
    }

    interface Evaluator {
        enum TYPE {
            NO_CRIMINAL_RECORD,
            MORE_EXPERIENCE,
            MORE_CREDIT_SCORE,
            NO_CRIMINAL_RECORD_AND_MORE_EXPERIENCE,
            MORE_EXPERIENCE_AND_MORE_CREDIT_SCORE,
            NO_CRIMINAL_RECORD_AND_MORE_CREDIT_SCORE,
            INVALID // should throw exception
        }

        boolean evaluate(Applicant applicant);
    }

    static class EvaluatorBuilder {
        private static final int SENIOR_EXP = 10;
        private static final int CREDIT_EXP = 500;

        public static Evaluator build(Evaluator.TYPE type) throws InvalidEvaluation {
            Evaluator NO_CRIMINAL_RECORD = a -> !a.hasCriminalRecord();
            Evaluator MORE_EXPERIENCE = a -> a.getEmploymentYears() >= SENIOR_EXP;
            Evaluator MORE_CREDIT_SCORE = a -> a.getCreditScore() >= CREDIT_EXP;

            if (type.equals(Evaluator.TYPE.NO_CRIMINAL_RECORD))
                return NO_CRIMINAL_RECORD;
            if (type.equals(Evaluator.TYPE.MORE_EXPERIENCE))
                return MORE_EXPERIENCE;
            if (type.equals(Evaluator.TYPE.MORE_CREDIT_SCORE))
                return MORE_CREDIT_SCORE;
            if (type.equals(Evaluator.TYPE.NO_CRIMINAL_RECORD_AND_MORE_EXPERIENCE))
                return a -> NO_CRIMINAL_RECORD.evaluate(a) && MORE_EXPERIENCE.evaluate(a);
            if (type.equals(Evaluator.TYPE.MORE_EXPERIENCE_AND_MORE_CREDIT_SCORE))
                return a -> MORE_EXPERIENCE.evaluate(a) && MORE_CREDIT_SCORE.evaluate(a);
            if (type.equals(Evaluator.TYPE.NO_CRIMINAL_RECORD_AND_MORE_CREDIT_SCORE))
                return a -> NO_CRIMINAL_RECORD.evaluate(a) && MORE_CREDIT_SCORE.evaluate(a);
            else throw new InvalidEvaluation();
        }
    }

}

