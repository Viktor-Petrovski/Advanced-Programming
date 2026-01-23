import java.util.*;

public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }

    static class Comment implements Comparable<Comment> {
        private final String content;
        private final String writtenBy;
        private final List<Comment> replies;
        private int likes;

        Comment(String content, String writtenBy) {
            this.content = content;
            this.writtenBy = writtenBy;

            replies = new ArrayList<>();
            likes = 0;
        }

        void like() {
            likes++;
        }

        void reply(Comment reply) {
            replies.add(reply);
        }

        public String info(int lvl) {
            replies.sort(Comparator.naturalOrder());
            String ws = "        " + "    ".repeat(lvl);
            StringBuilder sb = new StringBuilder();

            sb.append(ws).append("Comment: ").append(content).append("\n");
            sb.append(ws).append("Written by: ").append(writtenBy).append("\n");
            sb.append(ws).append("Likes: ").append(likes).append("\n");

            replies.forEach(r -> sb.append(r.info(lvl + 1)));
            return sb.toString();
        }

        private int innerLikes() {
            return likes + replies.stream().mapToInt(Comment::innerLikes).sum();
        }

        @Override
        public int compareTo(Comment o) {
            return Integer.compare(o.innerLikes(), this.innerLikes());
        }
    }

    static class Post {
        private final String username;
        private final String postContent;

        private final Map<String, Comment> commentMap;
        private final List<Comment> direct;

        Post(String username, String postContent) {
            this.username = username;
            this.postContent = postContent;

            commentMap = new HashMap<>();
            direct = new ArrayList<>();
        }

        ///  метод за додавање на коментар со ИД commentId и содржина content од корисникот со корисничко име username.
        ///  Коментарот може да биде директно на самата објава (replyToId=null во таа ситуација) или да биде reply на веќе постоечки коментар/reply. **
        void addComment (String username, String commentId, String content, String replyToId) {
            Comment ins = new Comment(content, username);
            commentMap.put(commentId, ins);

            if (replyToId != null && !replyToId.isEmpty()){
                Comment repliedTo = commentMap.get(replyToId);
                repliedTo.reply(ins);
            }
            else direct.add(ins);
        }

        /// метод за лајкнување на коментар
        void likeComment (String commentId) {
            commentMap.get(commentId).like();
        }

        @Override
        public String toString() {
            direct.sort(Comparator.naturalOrder());

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Post: %s\n",postContent));
            sb.append(String.format("Written by: %s\n",username));
            sb.append("Comments:\n");

            direct.forEach(c -> sb.append(c.info(0)));

            return sb.toString();
        }
    }
}
