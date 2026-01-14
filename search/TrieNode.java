package search;
import java.util.*;

class TrieNode {

    Map<Character, TrieNode> children = new HashMap<>();
    boolean isWord = false;
    int frequency = 0;

    List<String> topK = new ArrayList<>();

    void updateTopK(String word, int k) {
        if (!topK.contains(word)) {
            topK.add(word);
        }
        // simple popularity ordering CHANGE LATER
        if (topK.size() > k) {
            topK.remove(topK.size() - 1);
        }
    }
}
