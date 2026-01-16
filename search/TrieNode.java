package search;
import java.util.*;

class TrieNode {

    Map<Character, TrieNode> children = new HashMap<>();

    boolean isWord = false;

    List<String> topK = new ArrayList<>();

    Set<String> topKSet = new HashSet<>();
}

