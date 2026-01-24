import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class RaceTest {
    public static void main(String[] args) {
        TeamRace.findBestTeam();
    }

    static class Participant {
        private final String id;
        private final LocalTime start;
        private final LocalTime end;

        private Participant(String id, LocalTime start, LocalTime end) {
            this.id = id;
            this.start = start;
            this.end = end;
        }

        static Participant create(String s) {
            String[] tokens = s.split(" ");
            String id = tokens[0];
            LocalTime start = LocalTime.parse(tokens[1]);
            LocalTime end = LocalTime.parse(tokens[2]);

            return new Participant(id, start, end);
        }

        LocalTime totalTime() {
            LocalTime res = end;

            res = res.minusHours(start.getHour());
            res = res.minusMinutes(start.getMinute());
            res = res.minusSeconds(start.getSecond());

            return res;
        }

        @Override
        public String toString() {
            return String.format("%s %s", id, totalTime());
        }

        public static final Comparator<Participant> BY_TOTAL_TIME = Comparator.comparing(Participant::totalTime);
    }

    static class TeamRace {
        private static final List<Participant> participantList;

        static {
            participantList = new ArrayList<>();
        }

        static void findBestTeam() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.lines().map(Participant::create).forEach(participantList::add);

            List<Participant> top4 = participantList.stream().sorted(Participant.BY_TOTAL_TIME).limit(4)
                    .collect(Collectors.toCollection(ArrayList::new));

            // ID START_TIME END_TIME (пр. 1234 08:00:05 08:31:26)
            PrintWriter pw = new PrintWriter(System.out);
            top4.forEach(System.out::println);
            LocalTime res = LocalTime.MIDNIGHT;
            for (Participant p : top4) {
                LocalTime t = p.totalTime();
                res = res.plusHours(t.getHour());
                res = res.plusMinutes(t.getMinute());
                res = res.plusSeconds(t.getSecond());
            }
            pw.println(res);

            pw.flush();
        }


    }
}