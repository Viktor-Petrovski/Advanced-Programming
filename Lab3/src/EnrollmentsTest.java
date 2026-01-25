import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


public class EnrollmentsTest {

    static class SubjectWithGrade {
        private final String subject;
        private final int grade;

        public SubjectWithGrade(String subject, int grade) {
            this.subject = subject;
            this.grade = grade;
        }

    }

    static class EnrollmentsIO {
        static StudyProgramme getProgram(List<StudyProgramme> studyProgrammes, String progCode) {
            Optional<StudyProgramme> res = studyProgrammes.stream().filter(s -> s.getCode().equals(progCode)).findFirst();
            return res.orElse(null);
        }

        public static void readEnrollments(List<StudyProgramme> studyProgrammes, InputStream inputStream) {
            Scanner sc = new Scanner(inputStream);
            while (sc.hasNextLine()) {
                String l = sc.nextLine();
                if (l.isEmpty())
                    break;
                String[] line = l.split(";");

                int id = Integer.parseInt(line[0]);
                String name = line[1];
                double gpa = Double.parseDouble(line[2]);
                List<SubjectWithGrade> subjectsWithGrades = getSubjectWithGrades(line);
                String code = line[11];
                StudyProgramme ins = getProgram(studyProgrammes, code);
                Applicant applicant = new Applicant(id, name, gpa, ins, subjectsWithGrades);
                ins.addApplicant(applicant);
            }
        }

        private static List<SubjectWithGrade> getSubjectWithGrades(String[] line) {
            SubjectWithGrade sg1 = new SubjectWithGrade(line[3], Integer.parseInt(line[4]));
            SubjectWithGrade sg2 = new SubjectWithGrade(line[5], Integer.parseInt(line[6]));
            SubjectWithGrade sg3 = new SubjectWithGrade(line[7], Integer.parseInt(line[8]));
            SubjectWithGrade sg4 = new SubjectWithGrade(line[9], Integer.parseInt(line[10]));
            List<SubjectWithGrade> subjectsWithGrades = new ArrayList<>();
            subjectsWithGrades.add(sg1);
            subjectsWithGrades.add(sg2);
            subjectsWithGrades.add(sg3);
            subjectsWithGrades.add(sg4);
            return subjectsWithGrades;
        }

        public static void printRanked(List<Faculty> faculties) {
            for (Faculty f : faculties)
                System.out.print(f);
        }


    }

    public static void main(String[] args) {
        Faculty finki = new Faculty("FINKI");
        finki.addSubject("Mother Tongue");
        finki.addSubject("Mathematics");
        finki.addSubject("Informatics");

        Faculty feit = new Faculty("FEIT");
        feit.addSubject("Mother Tongue");
        feit.addSubject("Mathematics");
        feit.addSubject("Physics");
        feit.addSubject("Electronics");

        Faculty medFak = new Faculty("MEDFAK");
        medFak.addSubject("Mother Tongue");
        medFak.addSubject("English");
        medFak.addSubject("Mathematics");
        medFak.addSubject("Biology");
        medFak.addSubject("Chemistry");

        StudyProgramme si = new StudyProgramme("SI", "Software Engineering", finki, 4, 4);
        StudyProgramme it = new StudyProgramme("IT", "Information Technology", finki, 2, 2);
        finki.addStudyProgramme(si);
        finki.addStudyProgramme(it);

        StudyProgramme kti = new StudyProgramme("KTI", "Computer Technologies and Engineering", feit, 3, 3);
        StudyProgramme ees = new StudyProgramme("EES", "Electro-energetic Systems", feit, 2, 2);
        feit.addStudyProgramme(kti);
        feit.addStudyProgramme(ees);

        StudyProgramme om = new StudyProgramme("OM", "General Medicine", medFak, 6, 6);
        StudyProgramme nurs = new StudyProgramme("NURS", "Nursing", medFak, 2, 2);
        medFak.addStudyProgramme(om);
        medFak.addStudyProgramme(nurs);

        List<StudyProgramme> allProgrammes = new ArrayList<>();
        allProgrammes.add(si);
        allProgrammes.add(it);
        allProgrammes.add(kti);
        allProgrammes.add(ees);
        allProgrammes.add(om);
        allProgrammes.add(nurs);

        EnrollmentsIO.readEnrollments(allProgrammes, System.in);

        List<Faculty> allFaculties = new ArrayList<>();
        allFaculties.add(finki);
        allFaculties.add(feit);
        allFaculties.add(medFak);

        allProgrammes.forEach(StudyProgramme::calculateEnrollmentNumbers);

        EnrollmentsIO.printRanked(allFaculties);

    }

    static class Applicant {
        private static final int COEFFICIENT = 12;
        private final int id;
        private final String name;
        private final double gpa;


        private double points;
        private final List<SubjectWithGrade> subjectsWithGrades;
        StudyProgramme sp;

        public Applicant(int id, String name, double gpa, StudyProgramme sp, List<SubjectWithGrade> subjectsWithGrades) {
            this.id = id;
            this.name = name;
            this.gpa = gpa;
            this.sp = sp;
            this.subjectsWithGrades = subjectsWithGrades;
        }

//        void addSubjectAndGrade(String subject, int grade) {
//            SubjectWithGrade s = new SubjectWithGrade(subject, grade);
//            subjectsWithGrades.add(s);
//        }

        static double appropriate(List<String> appropriate, SubjectWithGrade subject) {
            return appropriate.contains(subject.subject) ? subject.grade * 2 : subject.grade * 1.2;
        }

        double calculatePoints(List<String> appropriate) {
            double r = gpa * COEFFICIENT;
            points = subjectsWithGrades.stream().mapToDouble(a -> Applicant.appropriate(appropriate, a)).sum() + r;
            return points;
        }

        @Override
        public String toString() {
            return String.format("Id: %d, Name: %s, GPA: %.1f - %f", id, name, gpa, points);
        }

    }

    static class StudyProgramme {
        private final String code;
        private final String name;
        private final Faculty faculty;
        private final int numPublicQuota;
        private final int numPrivateQuota;
        private int enrolledInPublicQuota;
        private int enrolledInPrivateQuota;
        private final List<Applicant> applicants = new ArrayList<>();

        private List<Applicant> publicQ = new ArrayList<>();
        private List<Applicant> privateQ = new ArrayList<>();
        private List<Applicant> rejected = new ArrayList<>();

        public void addApplicant(Applicant applicant) {
            applicants.add(applicant);
        }

        public StudyProgramme(String code, String name, Faculty faculty, int numPublicQuota, int numPrivateQuota) {
            this.code = code;
            this.name = name;
            this.faculty = faculty;
            this.numPublicQuota = numPublicQuota;
            this.numPrivateQuota = numPrivateQuota;
        }

        void calculateEnrollmentNumbers() {
            List<Applicant> sorted = applicants.stream()
                    .sorted(Comparator.comparing((Applicant a) -> a.calculatePoints(faculty.appropriateSubjects))
                            .reversed()).collect(Collectors.toCollection(ArrayList::new));

            publicQ = sorted.stream().limit(numPublicQuota).collect(Collectors.toList());
            privateQ = sorted.stream().skip(numPublicQuota).limit(numPrivateQuota).collect(Collectors.toList());
            rejected = sorted.stream().skip(numPublicQuota + numPrivateQuota).collect(Collectors.toList());

            enrolledInPublicQuota = publicQ.size();
            enrolledInPrivateQuota = privateQ.size();
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Name: %s", name)).append("\nPublic Quota:\n");
            for (Applicant a : publicQ)
                sb.append(a.toString()).append("\n");
            sb.append("Private Quota:\n");
            for (Applicant a : privateQ)
                sb.append(a.toString()).append("\n");
            sb.append("Rejected:\n");
            for (Applicant a : rejected)
                sb.append(a.toString());

            return sb.append("\n").toString();
        }

        public double cmp() {
            return (double) (enrolledInPublicQuota + enrolledInPrivateQuota) / (numPublicQuota + numPrivateQuota) * 100;
        }

        public static final Comparator<StudyProgramme> ORDER_BY_ENROLLMENTS =
                Comparator.comparing(StudyProgramme::cmp);
    }

    static class Faculty {
        private final String shortname;
        private final List<String> appropriateSubjects = new ArrayList<>();
        private List<StudyProgramme> studyProgrammes = new ArrayList<>();

        public Faculty(String shortname) {
            this.shortname = shortname;
        }

        @Override
        public String toString() {
            studyProgrammes = studyProgrammes.stream()
                    .sorted(StudyProgramme.ORDER_BY_ENROLLMENTS.reversed())
                    .collect(Collectors.toList());

            StringBuilder sb = new StringBuilder();
            sb.append("Faculty: ").append(shortname).append("\n");
            sb.append("Subjects: [");
            String subjects = String.join(", ", appropriateSubjects);
            sb.append(subjects).append("]\n").append("Study Programmes:\n");
            for (StudyProgramme s : studyProgrammes)
                sb.append(s.toString());
            return sb.toString();
        }

        public void addSubject(String subject) {
            appropriateSubjects.add(subject);
        }

        public void addStudyProgramme(StudyProgramme sp) {
            studyProgrammes.add(sp);
        }
    }

}
