package search;

import java.util.*;

public class UmbrellaClass {

    private final SearchEngine searchEngine = new SearchEngine();
    private final AutocompleteEngine autocompleteEngine = new AutocompleteEngine();

    public void ingest(int docId, String text) {

        String normalized = TextProcessor.normalizer(text);
        List<String> tokens = TextProcessor.tokenize(normalized);

        //init doc Micro optimization
        searchEngine.beginDoc(docId, tokens.size());

        for (String token : tokens) {
            searchEngine.TokenIngest(docId, token);
            autocompleteEngine.insert(token);
        }
    }

    // exposed APIs
    public List<String> autocomplete(String prefix) {
        return autocompleteEngine.suggest(prefix);
    }

}
