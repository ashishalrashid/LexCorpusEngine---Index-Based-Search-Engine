
import java.util.HashMap;
public class freq_counter {
    public static HashMap<Character,Integer> f_count(String s){
        HashMap<Character, Integer> map =new HashMap<>();
        for (char  c: s.toCharArray()){
            map.put(c,map.getOrDefault(c,0)+1);
        }
        return map;
    }

}
