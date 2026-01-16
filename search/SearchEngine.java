package search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Collections;

public class SearchEngine {
    //instance objects
    private Map<String, Map<Integer,Integer>> ReverseIndex;
    private Map<Integer, Map<String,Integer>> ForwardIndex;
    private int TotalTokens;
    private double AvgDocLength;

    public SearchEngine(){
        ForwardIndex= new HashMap<>();
        ReverseIndex=new HashMap<>();
    }
    
    //Ingester function - make a delete function as well(to delete ingestions)
    public void ingest(int doc_id,String text){

        String NormalText=TextProcessor.normalizer(text);
        List<String> tokens=TextProcessor.tokenize(NormalText);

        TotalTokens+=tokens.size();
        
        
        ForwardIndex.putIfAbsent(doc_id,new HashMap<>());
        Map<String,Integer> SubFMap=ForwardIndex.get(doc_id);

        for (String token: tokens){
            //foward mapping
            SubFMap.put(token,SubFMap.getOrDefault(token,0)+1);

            //reverse mapping
            ReverseIndex.putIfAbsent(token,new HashMap<>());
            Map<Integer,Integer> SubRMap=ReverseIndex.get(token);
            SubRMap.put(doc_id,SubRMap.getOrDefault(doc_id,0)+1);
        }
        //update internal variables -required for bm25
        TotalTokens +=tokens.size();
        AvgDocLength = TotalTokens / (double) ForwardIndex.size();



    }

    //single token ingestion
    public void beginDoc(int docId , int estimatedTokens){
        ForwardIndex.putIfAbsent(docId,new HashMap<>(estimatedTokens*2));
    }

    public void TokenIngest(int docId, String token){

        //forward Index
        Map<String, Integer> docMap =ForwardIndex.get(docId);
        docMap.put(token, docMap.getOrDefault(token,0)+1);

        //reverse Index
        Map<Integer,Integer> tokenDocs= ReverseIndex.computeIfAbsent(token,t->new HashMap<>());
        tokenDocs.put(docId, tokenDocs.getOrDefault(docId,0)+1);

        TotalTokens++;
        AvgDocLength=TotalTokens/(double) ForwardIndex.size();
    }

    //token and doc id are given to construct the forward and reverse index: 

    //Query Functions
    //public function needed , seach
    //seach-> get >normalize > tokenize > order > score >rank > return 
    public List<Integer> search(String query, int k){
        String NormalQuery=TextProcessor.normalizer(query);
        List<String> QueryTokens=TextProcessor.tokenize(NormalQuery);

        List<String> OrderedQueryTokens= OrderTokens(QueryTokens);

        Set<Integer> Candidates=GetCandidates(OrderedQueryTokens);

        List<Integer> Results= ScoreBM25(Candidates,OrderedQueryTokens,k);

        return Results;
    }

    //private functions: ordering,get candidates , scoring , ranking
    
    //filtering and ordering , ordering happens after tokenization , so i think this is the correct way
   //Ordering
    private  List<String> OrderTokens(List<String> Tokens){
        List<String> Ordered =new ArrayList<>();
        
        for (String token : Tokens){
            if (ReverseIndex.containsKey(token)){
                Ordered.add(token);
            }
        Ordered.sort(Comparator.comparing(t->ReverseIndex.get(t).size()));

        
        }
        return Ordered;
    }

    //generate candidate list
    private Set<Integer> GetCandidates(List<String> OrderedQueryTokens){
        Set<Integer> candiates=null;

        for (String Token: OrderedQueryTokens){
            Map<Integer,Integer> tokenDocs= ReverseIndex.get(Token);
            
            if (tokenDocs==null){
                return Set.of();
            }
            if (candiates==null){
                candiates=tokenDocs.keySet();
            } else {
                candiates.retainAll(tokenDocs.keySet());
            }

            if (candiates.isEmpty()){
                break;
            }
            }
            return candiates ==null? Set.of() : candiates;
        }

    //helper function to get doc length
    private int getDocLength(int docId) {
    int length = 0;
    for (int freq : ForwardIndex.get(docId).values()) {
        length += freq;
    }
    return length;
    }

    //scoring fucntion to score and create heap 
    private List<Integer> ScoreBM25(Set<Integer> Candidates,  List<String> QueryTokens, int k){
        final double K1=1.5;
        final double B=0.75;

        PriorityQueue<Map.Entry<Integer,Double>> heap = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));

        int N =ForwardIndex.size();

        for (int docId: Candidates){
            double score=0.0;
            int docLen=getDocLength(docId);

            for (String token: QueryTokens){

                Map<Integer,Integer>  tokenDocs=ReverseIndex.get(token);
                if (tokenDocs == null) continue;

                Integer tfObj = tokenDocs.get(docId);
                if (tfObj==null) continue;

                int tf=tfObj;
                int df=tokenDocs.size();

                double idf = Math.log((N - df + 0.5) / (df + 0.5) + 1);

                double denom = tf + K1 * (1 - B + B * (docLen / AvgDocLength));

                score += idf * (tf * (K1 + 1)) / denom;
            }
            if (heap.size()<k){
                heap.offer(Map.entry(docId,score));
            } else if (score >heap.peek().getValue()){
                heap.poll();
                heap.offer(Map.entry(docId,score));
            }
            }
            List<Integer> result =new ArrayList<>();
            while (!heap.isEmpty()){
                result.add(heap.poll().getKey());
            }
            Collections.reverse(result);
            return result;
        }
}


