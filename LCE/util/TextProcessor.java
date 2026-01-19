package LCE.util;
import java.util.Arrays;
import java.util.List;

public class TextProcessor {

    //normalizer
    public static  String normalizer(String text){
        text=text.toLowerCase();
        text = text.replaceAll("[^a-z0-9 ]", " ");
        text=text.replaceAll("\\s+"," ").trim();
        return text;
    } 


    //tokenizer
    public static List<String> tokenize(String text){
        return Arrays.asList(text.split(" "));
    }


}   