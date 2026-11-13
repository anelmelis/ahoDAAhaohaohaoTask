import java.util.*;
public class TestRunner {

    public static void main(String[] args) {
        check(
                "Lecture example",
                "ushers",
                Arrays.asList("he", "she", "his", "hers"),
                "he:2:3", "she:1:3", "hers:2:5"
        );

        check(
                "Overlapping patterns",
                "aaa",
                Arrays.asList("a", "aa", "aaa"),
                "a:0:0", "a:1:1", "a:2:2",
                "aa:0:1", "aa:1:2", "aaa:0:2"
        );

        check(
                "No matches",
                "xyzxyz",
                Arrays.asList("abc", "def")
        );

        check(
                "Empty text",
                "",
                Arrays.asList("a", "abc")
        );

        check(
                "Duplicate patterns",
                "aba",
                Arrays.asList("aba", "aba"),
                "aba:0:2", "aba:0:2"
        );

        check(
                "Unicode characters",
                "ёжёж",
                Arrays.asList("ёж", "ж"),
                "ёж:0:1", "ёж:2:3",
                "ж:1:1", "ж:3:3"
        );



        Random random = new Random(42);

        for (int test = 0; test < 200; test++) {
            List<String> patterns = new ArrayList<>();

            int patternCount = 1 + random.nextInt(8);

            for (int p = 0; p < patternCount; p++) {
                patterns.add(randomString(
                        random, 1 + random.nextInt(6)));
            }

            String text = randomString(random, 100);

            AhoCorasick aho = new AhoCorasick(patterns);

            List<String> ahoResults = new ArrayList<>();
            for (AhoCorasick.Match m : aho.search(text)) {
                ahoResults.add(signature(
                        m.pattern, m.start, m.end));
            }

            List<String> kmpResults = new ArrayList<>();
            for (KMP.Match m : KMP.searchAll(text, patterns)) {
                kmpResults.add(signature(
                        m.pattern, m.start, m.end));
            }

            Collections.sort(ahoResults);
            Collections.sort(kmpResults);

            if (!ahoResults.equals(kmpResults)) {
                throw new AssertionError(
                        "Random test failed: " + test);
            }
        }

        System.out.println(
                "[PASS] 200 randomized comparison tests");

        System.out.println(
                "\nALL TESTS PASSED!");
    }

    private static void check(
            String name,
            String text,
            List<String> patterns,
            String... expected) {

        AhoCorasick aho = new AhoCorasick(patterns);

        List<String> actual = new ArrayList<>();

        for (AhoCorasick.Match m : aho.search(text)) {
            actual.add(signature(m.pattern, m.start, m.end));
        }

        List<String> expectedList =
                new ArrayList<>(Arrays.asList(expected));

        Collections.sort(actual);
        Collections.sort(expectedList);

        if (!actual.equals(expectedList)) {
            throw new AssertionError(
                    name + " failed!\nExpected: " +
                            expectedList + "\nActual: " + actual);
        }
        System.out.println("[PASS] " + name);
    }

    private static String signature(
            String pattern, int start, int end) {
        return pattern + ":" + start + ":" + end;
    }

    private static String randomString(
            Random random, int length) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(3)));
        }
        return sb.toString();
    }
}
