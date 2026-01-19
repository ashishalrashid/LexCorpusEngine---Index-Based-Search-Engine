package LCE;
import java.util.Arrays;
import java.util.List;

class TextProcessor {

    //normalizer
    static  String normalizer(String text){
        text=text.toLowerCase();
        text = text.replaceAll("[^a-z0-9 ]", " ");
        text=text.replaceAll("\\s+"," ").trim();
        return text;
    } 


    //tokenizer
    static List<String> tokenize(String text){
        return Arrays.asList(text.split(" "));
    }


}   