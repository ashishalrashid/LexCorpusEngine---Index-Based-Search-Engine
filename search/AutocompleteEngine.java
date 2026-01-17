package search;
import java.util.*;

public class AutocompleteEngine {

    private static final int K = 5;

    private TrieNode root = new TrieNode();
    private final Map<String, Integer> wordFreq = new HashMap<>();
    //deletions and rebuild parameter
    private int TokensDeletedSinceRebuild=0;
    private static final int Rebuild_Threshold=50_000; 



    public void insert(String word) {

        // update global frequency
        wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);

        TrieNode node = root;

        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
            updateTopK(node, word);
        }

        node.isWord = true;
    }

    public List<String> suggest(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return List.of();
        }

        return node.topK;
    }

    public void decrementFreq(Map<String, Integer> docTokens) {

    if (docTokens == null) return;

    for (Map.Entry<String, Integer> entry : docTokens.entrySet()) {

        String token = entry.getKey();
        int freq = entry.getValue();

        Integer current = wordFreq.get(token);
        if (current == null) continue;

        if (current <= freq) {
            wordFreq.remove(token);
        } else {
            wordFreq.put(token, current - freq);
        }

        TokensDeletedSinceRebuild++;
    }
}

    public void CheckAndRebuild(){
        if (TokensDeletedSinceRebuild>=Rebuild_Threshold){
            rebuild();
        }
    }

    public void rebuild(){
        TokensDeletedSinceRebuild=0;

        TrieNode newRoot =new  TrieNode(); 

        for (Map.Entry<String,Integer> entry : wordFreq.entrySet()){

            String token =entry.getKey();
            int freq =entry.getValue();

            if (freq<=0) continue;
            
            insertIntoTrie(newRoot,token,freq);
        }
        this.root=newRoot;
    }
    /* ---------- helpers ---------- */
    
    //used at insert
    private void updateTopK(TrieNode node, String word) { 

        List<String> topK = node.topK;
        Set<String> topKSet=node.topKSet;

        if (topKSet.contains(word)) {
            sortTopK(topK);
            return;
        }

        if (topK.size() < K) {
            topK.add(word);
            topKSet.add(word);
            sortTopK(topK);
            return;
        }

        String weakest = topK.get(topK.size() - 1);

        if (wordFreq.get(word) > wordFreq.get(weakest)) {
            topK.remove(topK.size() - 1);
            topKSet.remove(weakest);
            topK.add(word);
            topKSet.add(word);
            sortTopK(topK);
        }
    }
    // overide the sort function be in line with freq map || used at helper : updatetopK
    private void sortTopK(List<String> list) {
        list.sort((a, b) -> wordFreq.get(b) - wordFreq.get(a));
    }

    private void insertIntoTrie(TrieNode root, String word, int freq) {

    TrieNode node = root;

    for (char c : word.toCharArray()) {
        node = node.children.computeIfAbsent(c, k -> new TrieNode());
        updateTopK(node, word);
    }

    node.isWord = true;
}

}
