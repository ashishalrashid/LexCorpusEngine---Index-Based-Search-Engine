package engine.Umbrella;

import java.util.*;

import engine.autocomplete.AutocompleteEngine;
import engine.search.SearchEngine;
import engine.util.TextProcessor;

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

    public List<String> autocomplete(String prefix) {
        return autocompleteEngine.suggest(prefix);
    }

    public void DeleteDocument(int DocId){
        Map<String, Integer> DocTokens = searchEngine.getSubForwardIndex(DocId);

        if (DocTokens==null) return;

        searchEngine.deleteDoc(DocId);
        autocompleteEngine.decrementFreq(DocTokens);
        
    }

}
