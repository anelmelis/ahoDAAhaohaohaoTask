import java.util.*;

public class AhoCorasick {


    private static class Node {
        int[] next;
        int failure = 0;
        int outputLink = -1;
        List<Integer> patternIds = new ArrayList<>();

        Node(int alphabetSize) {
            next = new int[alphabetSize];
            Arrays.fill(next, -1);
        }
    }


    public static class Match {
        public final String pattern;
        public final int start;
        public final int end;

        Match(String pattern, int start, int end) {
            this.pattern = pattern;
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "\"" + pattern + "\" at [" +
                    start + ", " + end + "]";
        }
    }

    private final List<Node> nodes = new ArrayList<>();
    private final List<String> patterns = new ArrayList<>();
    private final Map<Character, Integer> alphabet = new HashMap<>();

    public AhoCorasick(List<String> inputPatterns) {
        if (inputPatterns == null) {
            throw new IllegalArgumentException("Patterns cannot be null");
        }


        for (String pattern : inputPatterns) {
            if (pattern == null || pattern.isEmpty()) {
                throw new IllegalArgumentException(
                        "Patterns must be non-empty");
            }
            patterns.add(pattern);

            for (int i = 0; i < pattern.length(); i++) {
                char c = pattern.charAt(i);

                if (!alphabet.containsKey(c)) {
                    alphabet.put(c, alphabet.size());
                }
            }
        }

        nodes.add(new Node(alphabet.size()));

        for (int id = 0; id < patterns.size(); id++) {
            insert(patterns.get(id), id);
        }
        buildFailureLinks();
    }



    private void insert(String pattern, int id) {
        int state = 0;

        for (int i = 0; i < pattern.length(); i++) {
            int c = alphabet.get(pattern.charAt(i));
            int child = nodes.get(state).next[c];

            if (child == -1) {
                child = nodes.size();
                nodes.get(state).next[c] = child;
                nodes.add(new Node(alphabet.size()));
            }
            state = child;
        }
        nodes.get(state).patternIds.add(id);
    }



    private void buildFailureLinks() {
        Queue<Integer> queue = new ArrayDeque<>();
        Node root = nodes.get(0);

        for (int c = 0; c < alphabet.size(); c++) {
            int child = root.next[c];

            if (child == -1) {
                root.next[c] = 0;
            } else {
                nodes.get(child).failure = 0;
                queue.add(child);
            }
        }

        while (!queue.isEmpty()) {
            int state = queue.remove();
            Node node = nodes.get(state);
            Node failNode = nodes.get(node.failure);

            for (int c = 0; c < alphabet.size(); c++) {
                int child = node.next[c];

                if (child == -1) {
                    node.next[c] = failNode.next[c];
                } else {
                    Node childNode = nodes.get(child);

                    childNode.failure = failNode.next[c];

                    Node failureTarget =
                            nodes.get(childNode.failure);

                    childNode.outputLink =
                            !failureTarget.patternIds.isEmpty()
                                    ? childNode.failure
                                    : failureTarget.outputLink;

                    queue.add(child);
                }
            }
        }
    }



    public List<Match> search(String text) {
        if (text == null) {
            throw new IllegalArgumentException(
                    "Text cannot be null");
        }

        List<Match> matches = new ArrayList<>();
        int state = 0;

        for (int i = 0; i < text.length(); i++) {
            Integer c = alphabet.get(text.charAt(i));

            state = (c == null)
                    ? 0
                    : nodes.get(state).next[c];

            int outputState = state;

            while (outputState != -1) {
                Node node = nodes.get(outputState);

                for (int id : node.patternIds) {
                    String pattern = patterns.get(id);

                    matches.add(new Match(
                            pattern,
                            i - pattern.length() + 1,
                            i
                    ));
                }
                outputState = node.outputLink;
            }
        }
        return matches;
    }
}
