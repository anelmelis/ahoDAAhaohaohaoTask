# Aho Corasick Algorithm

Aho Corasick is an algorithm that finds 
multiple words 
in a single string.The good thing is that we don't need to search for every word separately. 
We first build a Trie. 
It’s basically a tree with all of your patterns.
So if some patterns share starting letters 
then they can reuse paths.


Then we build the failure links with
BFS. BFS goes through the Trie level by level.
Failure links are useful if we fail to match the
next character. Instead of starting again, we move to another node and continue checking. 
It's the longest suffix that is also a prefix of any pattern.


After that, we can search in our text. We just read letters one by one.
When we find a pattern, we save its position. 
We also use output links because sometimes we can find more 
than one pattern at the same position. Like in "ushers" we can 
find "she", "he" and "hers".
For complexity, L is the total length of patterns, V 
is the number of nodes, A is the alphabet size, 
N is the text length and Z is the number of matches.
Building takes O(L + V * A), searching takes O(N + Z), 
and space is O(L + V * A).


So I think this algorithm is good for many patterns because
it checks the text only once, not again and again like KMP.