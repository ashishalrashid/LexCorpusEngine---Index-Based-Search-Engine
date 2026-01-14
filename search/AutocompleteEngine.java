package search;

import java.util.*;

public class AutocompleteEngine{

    private final TrieNode root = new TrieNode();
    private static final int K = 5;

    public void insert(String word) {
        TrieNode node = root;

        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
            node.updateTopK(word, K);
        }

        node.isWord = true;
        node.frequency++;
    }

    public List<String> suggest(String prefix) {
        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return List.of();
        }

        return node.topK;
    }
}
