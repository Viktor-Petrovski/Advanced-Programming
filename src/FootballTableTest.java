import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }

    static class Team implements Comparable<Team> {
        private final String name;
        private int wins;
        private int draws;
        private int losses;

        private int givenGoals;
        private int takenGoals;

        Team(String name) {
            this.name = name;

            wins = 0;
            draws = 0;
            losses = 0;

            givenGoals = 0;
            takenGoals = 0;
        }

        void update(int thisGoals, int otherGoals) {
            if (thisGoals > otherGoals)
                wins++;
            else if (thisGoals < otherGoals) losses++;
            else draws++;

            givenGoals += thisGoals;
            takenGoals += otherGoals;
        }

        private int points() {
            return wins * 3 + draws;
        }

        private int goalsDifference() {
            return givenGoals - takenGoals; // abs?
        }

        @Override
        public String toString() {
            //Во табелата се прикажуваат редниот број на тимот во табелата, името (со 15 места порамнето во лево),
            // бројот на одиграни натпревари, бројот на победи, бројот на нерешени натпревари, бројот на освоени поени
            // (сите броеви се печатат со 5 места порамнети во десно)
            int plays = wins + draws + losses;
            return String.format("%-15s%5d%5d%5d%5d%5d", name, plays, wins, draws, losses, points());
        }

        @Override
        public int compareTo(Team o) {
            int i = Integer.compare(o.points(), this.points());
            if (i != 0) return i;
            i = Integer.compare(o.goalsDifference(), this.goalsDifference());
            return i != 0 ? i : name.compareTo(o.name);
        }
    }

    static class FootballTable {
        private final Map<String, Team> teamMap; // name -> team

        FootballTable() {
            teamMap = new HashMap<>();
        }

        public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
            Team home = teamMap.computeIfAbsent(homeTeam, k -> new Team(homeTeam));
            Team away = teamMap.computeIfAbsent(awayTeam, k -> new Team(awayTeam));

            home.update(homeGoals, awayGoals);
            away.update(awayGoals, homeGoals);
        }

        void printTable() {
            List<Team> res = teamMap.values().stream().sorted().collect(Collectors.toCollection(ArrayList::new));

            IntStream.range(0, res.size()).forEach(i ->
                System.out.printf("%2d. %s\n", i + 1, res.get(i))
            );
        }
    }
}
