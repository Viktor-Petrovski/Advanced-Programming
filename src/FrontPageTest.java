import java.util.*;
import java.util.stream.Collectors;

public class FrontPageTest {
    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            cal = Calendar.getInstance();
            int min = scanner.nextInt();
            cal.add(Calendar.MINUTE, -min);
            Date date = cal.getTime();
            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -min);
            scanner.nextLine();
            Date date = cal.getTime();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for(Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch(CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    static class CategoryNotFoundException extends Exception {
        public CategoryNotFoundException(String category) {
            super(String.format("Category %s was not found", category));
        }
    }

    static class Category {
        final String name;

        Category(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Category category = (Category) o;
            return Objects.equals(name, category.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

    static abstract class NewsItem {
        protected final String title;
        protected final Date date;
        protected final Category category;

        NewsItem(String title, Date date, Category category) {
            this.title = title;
            this.date = date;
            this.category = category;
        }

        public Category getCategory() {
            return category;
        }

        abstract String getTeaser();

        @Override
        public String toString() {
            return getTeaser(); // повикува конкретна имплементација
        }

    }

    static class TextNewsItem extends NewsItem {
        private final String text;

        TextNewsItem(String title, Date date, Category category, String text) {
            super(title, date, category);
            this.text = text;
        }


        /// враќа String составен од насловот на веста,
        /// пред колку минути е објавена веста (цел број минути) и максимум 80 знаци од содржината на веста,
        /// сите одделени со нов ред
        @Override
        String getTeaser() {
            long mins = (new Date().getTime() - date.getTime()) / 60000;
            String shortForm = text.length() > 80 ? text.substring(0, 80) : text;
            return String.format("%s\n%d\n%s\n", title, mins, shortForm);
        }

    }

    static class MediaNewsItem extends NewsItem {
        private final String url;
        private final int views;

        MediaNewsItem(String title, Date date, Category category, String url, int views) {
            super(title, date, category);
            this.url = url;
            this.views = views;
        }


        /// враќа String составен од насловот на веста, пред колку минути е објавена веста (цел број минути),
        /// url-то на веста и бројот на погледи, сите одделени со нов ред.
        @Override
        String getTeaser() {
            long mins = (new Date().getTime() - date.getTime()) / 60000;
            return String.format("%s\n%d\n%s\n%d\n", title, mins, url, views);
        }

    }

    static class FrontPage {
        private final Category[] categories;
        private final List<NewsItem> newsItems;

        FrontPage(Category[] categories) {
            this.categories = categories;
            newsItems = new ArrayList<>();
        }

        void addNewsItem(NewsItem newsItem) {
            newsItems.add(newsItem);
        }

        List<NewsItem> listByCategory(Category category) {
            return newsItems.stream()
                    .filter(i -> i.getCategory().equals(category))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        List<NewsItem> listByCategoryName(String category) throws CategoryNotFoundException {
            if (Arrays.stream(categories).noneMatch(c -> c.name.equals(category)))
                throw new CategoryNotFoundException(category);

            return newsItems.stream()
                    .filter(i -> i.getCategory().name.equals(category))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            newsItems.forEach(i -> sb.append(i.toString()));
            return sb.toString();
        }
    }
}
