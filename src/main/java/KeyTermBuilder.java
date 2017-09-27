import java.util.ArrayList;

/**
 * Created by zhangtao on 27/9/17.
 */
public class KeyTermBuilder {
    ArrayList<Sentence> sentences;
    ArrayList<String> keyTerms;
    public KeyTermBuilder(ArrayList<Sentence> sentences){
        this.sentences = sentences;
        this.keyTerms = new ArrayList<String>();
    }

    public void buildKeyTermsBySectionName(){
        for(Sentence s : sentences){
            if(s.sentenceNum == -1){
                if(s.sentence.length()<30){
                    keyTerms.add(s.sentence);
                }
            }
        }
    }

    public ArrayList<String> getKeyTerms(){
        return this.keyTerms;
    }

}
