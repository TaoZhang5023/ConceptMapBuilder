import java.util.ArrayList;
import java.util.*;

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
//        String fileName = "test.txt";
//        TextReader textReader = new TextReader(fileName);
//        textReader.scanFile();
//        ArrayList<Sentence> sentences = textReader.getSentences();
//        for(Sentence s: sentences){
//            System.out.println(s.sentenceNum + " " + s.sentence);
//        }
//
//        KeyTermBuilder ktb = new KeyTermBuilder(sentences);
//        ArrayList<String> keyTerms = ktb.getKeyTerms();
//        ktb.buildKeyTermsBySectionName();
//        for(String k: keyTerms){
//            System.out.println(k);
//        }
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // read some text in the text variable
        String text = "For loop\n" +
                "\n" +
                "In computer science, a for-loop (or simply for loop) is a control flow statement for specifying iteration, which allows code to be executed repeatedly. The syntax of a for-loop is based on the heritage of the language and the prior programming languages it borrowed from, so programming languages that are descendants of or offshoots of a language that originally provided an iterator will often use the same keyword to name an iterator, e.g., descendants of ALGOL use \"for\", while descendants of Fortran use \"do.\" There are other possibilities, for example COBOL which uses \"PERFORM VARYING\".\n" +
                "\n" +
                "Unlike many other kinds of loops, such as the while-loop, the for-loop is often distinguished by an explicit loop counter or loop variable. This allows the body of the for-loop (the code that is being repeatedly executed) to know about the sequencing of each iteration. For-loops are also typically used when the number of iterations is known before entering the loop. For-loops are the shorthand way to make loops when the number of iterations is known, as every for-loop could be written as a while-loop.\n" +
                "\n" +
                "The name for-loop comes from the English word for, which is used as the keyword in most programming languages to introduce a for-loop. The term in English dates to ALGOL 58 and was popularized in the influential later ALGOL 60; it is the direct translation of the earlier German für, used in Superplan (1949–1951) by Heinz Rutishauser, who also was involved in defining ALGOL 58 and ALGOL 60. The loop body is executed \"for\" the given values of the loop variable, though this is more explicit in the ALGOL version of the statement, in which a list of possible values and/or increments can be specified.\n" +
                "\n" +
                "In FORTRAN and PL/I though, the keyword DO is used and it is called a do-loop, but it is otherwise identical to the for-loop described here and is not to be confused with the do-while loop.";
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            }

            // this is the parse tree of the current sentence
            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

            // this is the Stanford dependency graph of the current sentence
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            Map<Integer, CorefChain> graph =
                    document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
            for(Map.Entry g: graph.entrySet()){
                System.out.println(g.getKey() + ", " + g.getValue());
            }
        }


    }
}
