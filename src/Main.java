import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // Test 1: Example from the lecture
        List<String> patterns =
                Arrays.asList("he", "she", "his", "hers");

        AhoCorasick aho = new AhoCorasick(patterns);

        System.out.println("Text: ushers");

        for (AhoCorasick.Match match : aho.search("ushers")) {
            System.out.println(match);
        }

        // Test 2: Overlapping matches
        System.out.println("\nOverlapping matches in aaa:");

        AhoCorasick overlap = new AhoCorasick(
                Arrays.asList("a", "aa", "aaa")
        );
        for (AhoCorasick.Match match : overlap.search("aaa")) {
            System.out.println(match);
        }
    }
}
