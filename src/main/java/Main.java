import java.io.*;
import java.util.*;

import TripleExtractor.TripleExtractor;
import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class Main {
    public static void main(String[] args) {
        String fileName = "test.txt";
        String text = "";
        BufferedReader br = null;
        FileReader fr = null;
        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                text += sCurrentLine + " ";
                System.out.println(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
//        TextReader textReader = new TextReader(fileName);
//        textReader.scanFile();
//        ArrayList<Sentence> sentenceList = textReader.getSentences();
//        // read some text in the text variable
//        String text = "";
//        for(Sentence s: sentenceList){
//            //System.out.println(s.sentenceNum + " " + s.sentence);
//            System.out.println(s.sentence);
//            text += s.sentence + " ";
//        }


//        KeyTermBuilder ktb = new KeyTermBuilder(sentences);
//        ArrayList<String> keyTerms = ktb.getKeyTerms();
//        ktb.buildKeyTermsBySectionName();
//        for(String k: keyTerms){
//            System.out.println(k);
//        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("output.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);


        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        pipeline.annotate(document);
        writer.println("===========original text===========");
        writer.println(text);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods

            writer.println("===========Pos and Ner tag===========");
            writer.println("word" + "\t\t" + "pos" + "\t\t" + "ne");

            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                writer.println(word + "\t\t" + pos + "\t\t" + ne);
            }
            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            TripleExtractor tripleExtractor = new TripleExtractor();
            tripleExtractor.extractTriplet(tree);
            String resultOfTree = tree.pennString();
            writer.println("===========result of tree===========");
            writer.println(resultOfTree);

            // this is the Stanford dependency graph of the current sentence
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            String resultOfGraph = dependencies.toCompactString();
            writer.println("===========result of graph===========");
            writer.println(resultOfGraph);
        }
        Map<Integer, CorefChain> graph =
                document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        writer.println("===========result of map===========");
        for(Map.Entry g: graph.entrySet()){
            writer.println(g.getKey() + ", " + g.getValue());
        }
        writer.flush();

    }
}
