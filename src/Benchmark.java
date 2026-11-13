import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Benchmark {

    private static final int[] PATTERN_COUNTS = {10, 100, 1000};
    private static final int[] TEXT_LENGTHS = {1000, 10000, 100000};

    private static final int WARMUPS = 2;
    private static final int RUNS = 5;

    public static void main(String[] args) throws IOException {

        StringBuilder csv = new StringBuilder();

        csv.append("patterns,text_length,matches,aho_build_avg_ms,")
                .append("aho_search_avg_ms,aho_total_avg_ms,")
                .append("kmp_avg_ms,kmp_over_aho_total\n");

        System.out.printf(
                "%-9s %-11s %-9s %-12s %-13s %-13s %-12s %-10s%n",
                "Patterns", "Text", "Matches", "AC build",
                "AC search", "AC total", "KMP total", "KMP/AC"
        );

        for (int patternCount : PATTERN_COUNTS) {

            List<String> patterns = generatePatterns(patternCount);

            for (int textLength : TEXT_LENGTHS) {

                String text = generateText(
                        textLength, patterns, patternCount);

                verifySameMatches(
                        new AhoCorasick(patterns).search(text),
                        KMP.searchAll(text, patterns)
                );

                for (int w = 0; w < WARMUPS; w++) {
                    int a = new AhoCorasick(patterns)
                            .search(text).size();

                    int k = KMP.searchAll(text, patterns).size();

                    if (a != k) {
                        throw new AssertionError("Warm-up mismatch");
                    }
                }

                long buildNs = 0;
                long searchNs = 0;
                long kmpNs = 0;
                int matchCount = -1;

                for (int run = 0; run < RUNS; run++) {

                    long startBuild = System.nanoTime();

                    AhoCorasick aho = new AhoCorasick(patterns);

                    long endBuild = System.nanoTime();

                    List<AhoCorasick.Match> ahoMatches =
                            aho.search(text);

                    long endAhoSearch = System.nanoTime();

                    List<KMP.Match> kmpMatches =
                            KMP.searchAll(text, patterns);

                    long endKmp = System.nanoTime();

                    if (ahoMatches.size() != kmpMatches.size()) {
                        throw new AssertionError(
                                "Match count mismatch");
                    }

                    if (matchCount == -1) {
                        matchCount = ahoMatches.size();
                    }

                    if (matchCount != ahoMatches.size()) {
                        throw new AssertionError(
                                "Inconsistent match count");
                    }
                    buildNs += endBuild - startBuild;
                    searchNs += endAhoSearch - endBuild;
                    kmpNs += endKmp - endAhoSearch;
                }

                double buildMs =
                        buildNs / (RUNS * 1_000_000.0);

                double searchMs =
                        searchNs / (RUNS * 1_000_000.0);

                double ahoTotalMs = buildMs + searchMs;

                double kmpMs =
                        kmpNs / (RUNS * 1_000_000.0);

                double ratio = kmpMs / ahoTotalMs;

                System.out.printf(
                        Locale.US,
                        "%-9d %-11d %-9d %-12.3f %-13.3f " +
                                "%-13.3f %-12.3f %.2fx%n",
                        patternCount, textLength, matchCount,
                        buildMs, searchMs, ahoTotalMs,
                        kmpMs, ratio
                );

                csv.append(String.format(
                        Locale.US,
                        "%d,%d,%d,%.6f,%.6f,%.6f,%.6f,%.4f%n",
                        patternCount, textLength, matchCount,
                        buildMs, searchMs, ahoTotalMs,
                        kmpMs, ratio
                ));
            }
        }

        Path file = Path.of("results.csv");

        Files.writeString(
                file, csv.toString(), StandardCharsets.UTF_8);

        System.out.println(
                "\nSaved results: " + file.toAbsolutePath());
    }

    private static List<String> generatePatterns(int count) {

        Random random = new Random(7000L + count);
        Set<String> unique = new LinkedHashSet<>();

        while (unique.size() < count) {
            int length = 5 + random.nextInt(6);
            unique.add(randomString(random, length));
        }
        return new ArrayList<>(unique);
    }

    private static String generateText(
            int length, List<String> patterns, int patternCount) {

        Random random = new Random(70000L + length);

        char[] chars = randomString(
                random, length).toCharArray();

        Random insertRandom = new Random(
                900000L + 1000L * patternCount + length);

        for (int pos = 50; pos + 10 <= chars.length; pos += 200) {
            String pattern = patterns.get(
                    insertRandom.nextInt(patterns.size()));

            for (int j = 0; j < pattern.length(); j++) {
                chars[pos + j] = pattern.charAt(j);
            }
        }
        return new String(chars);
    }

    private static String randomString(Random random, int length) {

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }
        return sb.toString();
    }

    private static void verifySameMatches(
            List<AhoCorasick.Match> aho,
            List<KMP.Match> kmp) {

        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();

        for (AhoCorasick.Match m : aho) {
            a.add(m.pattern + ":" + m.start + ":" + m.end);
        }
        for (KMP.Match m : kmp) {
            b.add(m.pattern + ":" + m.start + ":" + m.end);
        }

        Collections.sort(a);
        Collections.sort(b);

        if (!a.equals(b)) {
            throw new AssertionError(
                    "Aho-Corasick and KMP returned different matches");
        }
    }
}
