import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class AdNetworkTest {
    public static final BufferedReader BR = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        AdNetwork network = new AdNetwork();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));

        int k = Integer.parseInt(BR.readLine().trim());

        if (k == 0) {
            network.readAds();
            network.placeAds(1, pw);
        } else if (k == 1) {
            network.readAds();
            network.placeAds(3, pw);
        } else {
            network.readAds();
            network.placeAds(8, pw);
        }

        pw.flush();
    }

    static class Ad implements Comparable<Ad> {
        private final String id;
        private final String category;
        private final double bidValue; // понуда за прикажување на рекламата (во долари)
        private final double ctr; // просечна стапка на кликнување (Click-Through Rate)
        private final String content;

        Ad(String id, String category, double bidValue, double ctr, String content) {
            this.id = id;
            this.category = category;
            this.bidValue = bidValue;
            this.ctr = ctr;
            this.content = content;
        }

        public String getCategory() {
            return category;
        }

        public double getBidValue() {
            return bidValue;
        }

        public double getCtr() {
            return ctr;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            // ID CATEGORY (bid=…, ctr=…%) CONTENT
            return String.format("%s %s (bid=%.2f, ctr=%.2f%%) %s",
                    id, category, bidValue, ctr * 100, content);
        }

        @Override
        public int compareTo(Ad o) {
            // „природниот редослед“ ќе биде по bidValue во опаѓачки редослед,
            // а доколку bidValue е ист, според id во растечки редослед.
            int i = Double.compare(o.bidValue, this.bidValue);
            return i != 0 ? i : id.compareTo(o.id);
        }
    }

    static class AdRequest {
        private final String id;
        private final String category;
        private final double floorBid; // минимална дозволена понуда за прикажување реклама
        private final String keywords; // клучни зборови поврзани со барањето (разделени со празно место)

        AdRequest(String id, String category, double floorBid, String keywords) {
            this.id = id;
            this.category = category;
            this.floorBid = floorBid;
            this.keywords = keywords;
        }

        public String getId() {
            return id;
        }

        public String getCategory() {
            return category;
        }

        public String getKeywords() {
            return keywords;
        }

        @Override
        public String toString() {
            // ID [CATEGORY] (floor=…): KEYWORDS
            return String.format("%s %s (floor=%.2f) %s\n",
                    id, category, floorBid, keywords);
        }
    }

    static class AdNetwork {
        private final List<Ad> ads;

        AdNetwork() {
            ads = new ArrayList<>();
        }

        void readAds() throws IOException {
            // ID CATEGORY BID_VALUE CTR CONTENT
            while(true) {
                String s = BR.readLine();
                if (s == null || s.isEmpty())
                    break;
                String[] tokens = s.split(" ");

                String id = tokens[0];
                String category = tokens[1];
                double bidValue = Double.parseDouble(tokens[2]);
                double ctr = Double.parseDouble(tokens[3]);
                String content = Arrays.stream(tokens).skip(4).collect(Collectors.joining(" "));

                ads.add(new Ad(id, category, bidValue, ctr, content));
            }
        }

        void placeAds(int k, PrintWriter pw) throws IOException {
            // ID CATEGORY FLOOR_BID KEYWORD1 KEYWORD2 KEYWORD3…
            // AR001 tech 2.0 technology phone application inches
            String[] tokens = BR.readLine().split(" ");

            String id = tokens[0];
            String category = tokens[1];
            double floorBid = Double.parseDouble(tokens[2]);
            String keywords = Arrays.stream(tokens).skip(3).collect(Collectors.joining(" "));

            AdRequest req = new AdRequest(id, category, floorBid, keywords);

            pw.printf("Top ads for request %s:\n", req.getId());

            double x = 5.0;
            double y = 100.0;
            Comparator<Ad> comparator = Comparator.comparing(a -> relevanceScore(a, req) + x * a.getBidValue() + y * a.getCtr());

            ads.stream().filter(a -> a.getBidValue() >= floorBid)
                    .sorted(comparator.reversed())
                    .limit(k)
                    .sorted(Comparator.naturalOrder())
                    .forEach(pw::println);

            pw.flush();

        }

        private int relevanceScore(Ad ad, AdRequest req) {
            int score = 0;
            if (ad.getCategory().equalsIgnoreCase(req.getCategory())) score += 10;
            String[] adWords = ad.getContent().toLowerCase().split("\\s+");
            String[] keywords = req.getKeywords().toLowerCase().split("\\s+");
            for (String kw : keywords) {
                for (String aw : adWords) {
                    if (kw.equals(aw)) score++;
                }
            }
            return score;
        }
    }

}
