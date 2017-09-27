/**
 * Created by zhangtao on 27/9/17.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class TextReader {
    String fileName;
    ArrayList<Sentence> sentences;
    public TextReader(String fileName){
        this.sentences = new ArrayList<Sentence>();
        this.fileName = fileName;
    }

    public void scanFile(){
        try  {
            Scanner sentence = new Scanner(new File(fileName));
            ArrayList<String> sentenceList = new ArrayList<String>();
            while(sentence.hasNextLine()){
                sentenceList.add(sentence.nextLine());
            }
            sentence.close();
            String[] sentenceArray = sentenceList.toArray(new String[sentenceList.size()]);

            for (int r=0;r<sentenceArray.length;r++)
            {
                //
                String [] sentenceLib = sentenceArray[r].split("(?<=[a-z])\\.\\s+");
                for (int i=0;i<sentenceLib.length;i++)
                {
                    Sentence temp;
                    if(sentenceLib[i].length()>0){
                        if(sentenceLib.length == 1){
                            temp = new Sentence(sentenceLib[i], -1);
                        }else{
                            temp = new Sentence(sentenceLib[i], i);
                        }
                        this.sentences.add(temp);
                    }
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Sentence> getSentences(){
        return this.sentences;
    }
}
