# Aho–Corasick Algorithm

## 1. Overview

This project implements the **Aho–Corasick algorithm** in Java to search for multiple patterns in a text efficiently. It also includes the KMP algorithm for performance comparison.

Aho–Corasick uses three main components:

- **Trie:** Stores all patterns in a prefix tree, sharing common prefixes.
- **Failure Links:** Built using BFS. They allow the algorithm to fall back to the longest matching suffix when a transition fails.
- **Output Links:** Report all matching patterns, including overlapping and suffix matches.

Unlike KMP, which searches for each pattern separately, Aho–Corasick finds all patterns in a single scan of the text.

## 2. Complexity Analysis

Let:
- N = text length
- L = total length of patterns
- P = number of patterns
- V = number of Trie nodes
- A = alphabet size
- Z = number of matches

| Algorithm | Preprocessing | Searching | Auxiliary Space |
|---|---|---|---|
| Aho–Corasick | O(L + V × A) | O(N + Z) | O(L + V × A) |
| KMP (all patterns) | O(L) | O(P × N) | O(max pattern length) |

The Aho–Corasick implementation uses precomputed transitions for each Trie node. This requires additional memory but allows efficient searching in O(N + Z).

Both implementations also require O(Z) space to store their reported matches.

## 3. Experimental Results

Experiments were conducted using:
- **Patterns:** 10, 100, 1,000
- **Text lengths:** 1,000, 10,000, 100,000
- **Runs:** 5 per configuration, after 2 warm-up runs
- **Measurement:** `System.nanoTime()`

Each value represents the average execution time in milliseconds. Aho–Corasick total time includes automaton construction and searching, while KMP includes preprocessing and searching for all patterns.

| Patterns | Text Length | Aho–Corasick (ms) | KMP (ms) |
|---|---|---|---|
| 10 | 1,000 | 0.449 | 0.531 |
| 10 | 10,000 | 0.498 | 0.342 |
| 10 | 100,000 | 0.877 | 1.992 |
| 100 | 1,000 | 0.404 | 0.243 |
| 100 | 10,000 | 0.487 | 2.318 |
| 100 | 100,000 | 0.943 | 20.806 |
| 1,000 | 1,000 | 1.120 | 2.079 |
| 1,000 | 10,000 | 1.007 | 19.156 |
| 1,000 | 100,000 | 1.772 | 181.834 |

Detailed results, including construction times, search times, and match counts, are available in `results.csv`.

## 4. Performance Comparison

Aho–Corasick performs better as the number of patterns increases because it searches all patterns simultaneously.

For **1,000 patterns and 100,000 characters**, Aho–Corasick was approximately **102.64 times faster** than KMP.

KMP was faster in some smaller test cases because Aho–Corasick requires additional preprocessing.

**Aho–Corasick advantages:** Efficient multi-pattern searching, single text traversal, supports overlapping matches.

**Aho–Corasick limitations:** Higher memory usage and additional preprocessing.

**KMP advantages:** Simple implementation, low memory usage, efficient for a single pattern.

**KMP limitations:** Requires scanning the text separately for each pattern.

## 5. Testing

The implementation was verified using:
- Standard lecture example
- Overlapping patterns
- No matches and empty text
- Duplicate patterns and Unicode characters
- 200 randomized comparisons with KMP

**All 206 tests passed successfully.**

## 6. Conclusion

The experiments confirm that Aho–Corasick is more suitable for searching many patterns in large texts, while KMP remains a good choice for searching individual patterns.