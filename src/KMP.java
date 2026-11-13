import java.util.ArrayList;
import java.util.List;

public class KMP {

    public static class Match {
        public final String pattern;
        public final int start;
        public final int end;

        public Match(String pattern, int start, int end) {
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

    private static int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else if (length != 0) {
                length = lps[length - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }

    public static List<Match> searchAll(
            String text, List<String> patterns) {

        if (text == null || patterns == null) {
            throw new IllegalArgumentException(
                    "Text and patterns cannot be null");
        }

        List<Match> matches = new ArrayList<>();

        for (String pattern : patterns) {
            if (pattern == null || pattern.isEmpty()) {
                throw new IllegalArgumentException(
                        "Patterns must be non-empty");
            }

            int[] lps = buildLPS(pattern);
            int i = 0;
            int j = 0;

            while (i < text.length()) {
                if (text.charAt(i) == pattern.charAt(j)) {
                    i++;
                    j++;

                    if (j == pattern.length()) {
                        matches.add(new Match(
                                pattern,
                                i - j,
                                i - 1
                        ));

                        j = lps[j - 1];
                    }
                } else if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }
}
