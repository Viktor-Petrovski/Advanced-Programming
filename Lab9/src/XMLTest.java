import java.util.*;
import java.util.stream.Collectors;


public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        if (testCase == 1) {
            //TODO Print the component object
            System.out.println(component.getRepresentation(0));
        } else if (testCase == 2) {
            //TODO print the composite object
            System.out.println(composite.getRepresentation(0));
        } else if (testCase == 3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level", "1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level", "2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level", "3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));

            System.out.println(main.getRepresentation(0));
            //TODO print the main object
        }
    }

    interface XMLComponent {
        void addAttribute(String attribute, String value);

        String getRepresentation(int lvl);
    }

    static class XMLLeaf implements XMLComponent {
        private final String tag;
        private final String value;

        private final Map<String, String> attributes;

        XMLLeaf(String tag, String value) {
            this.tag = tag;
            this.value = value;

            attributes = new LinkedHashMap<>();
        }

        @Override
        public void addAttribute(String attribute, String value) {
            attributes.put(attribute, value);
        }

        @Override
        public String getRepresentation(int lvl) {
            String listKV = attributes.entrySet().stream()
                    .map(e -> String.format("%s=\"%s\"", e.getKey(), e.getValue()))
                    .collect(Collectors.joining(" "));
            String pairs = listKV.isEmpty() ? "" : " " + listKV;
            String ws = "    ".repeat(lvl);
            return String.format("%s<%s%s>%s</%s>", ws, tag, pairs, value, tag);
        }
    }

    static class XMLComposite implements XMLComponent {
        private final String tag;
        private final Map<String, String> attributes;
        private final Collection<XMLComponent> components;

        XMLComposite(String tag) {
            this.tag = tag;

            attributes = new LinkedHashMap<>();
            components = new LinkedHashSet<>();
        }

        @Override
        public void addAttribute(String attribute, String value) {
            attributes.put(attribute, value);
        }

        public void addComponent(XMLComponent ins) {
            components.add(ins);
        }

        @Override
        public String getRepresentation(int lvl) {
            String listKV = attributes.entrySet().stream()
                    .map(e -> String.format("%s=\"%s\"", e.getKey(), e.getValue()))
                    .collect(Collectors.joining(" "));
            String pairs = listKV.isEmpty() ? "" : " " + listKV;

            String ws = "    ".repeat(lvl);

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%s<%s%s>\n", ws, tag, pairs));

            components.forEach(c -> sb.append(c.getRepresentation(lvl + 1)).append("\n"));

            sb.append(String.format("%s</%s>", ws, tag));
            return sb.toString();
        }

    }

}

