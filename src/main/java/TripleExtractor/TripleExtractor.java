package TripleExtractor;

import edu.stanford.nlp.trees.Tree;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by zhangtao on 28/9/17.
 */
public class TripleExtractor {
    class Verb {
        String type;
        String token;

        Verb(){
            this.type = "";
            this.token = "";
        }

        Verb(String type, String token){
            this.type = type;
            this.token = token;
        }
    }

    private String getNoun(Tree tree){
        String subject = "";
        for(int i=0;i<tree.children().length;i++){
            if(tree.children()[i].value().equals("NN") || tree.children()[i].value().equals("DT")
                    || tree.children()[i].value().equals("JJ")){
                if(!tree.children()[i].value().equals("DT")){
                    subject += tree.children()[i].children()[0].value() + " ";
                }
            }else{
                break;
            }
        }
        return subject;
    }


    public String BFSforNPSubTrees(Tree tree){
        //case1. the NP has only one noun which is subject
        //case2. the NP itself is subject
        //case3. Use the lowest NP as Subject
        //case4. Use the first NN as Subject
        //System.out.println("input: " + tree);
        String subject = "";
        Tree[] children = tree.children();
        for(Tree t: children){
            String value = t.value();
            boolean isNoun = value.equals("NN") || value.equals("NNS")
                    || value.equals("NNP") || value.equals("NNPS");
            boolean isItself = value.equals("DT");
            if(isNoun) {
                subject = t.children()[0].value();
            }else if(isItself){
                subject = getNoun(tree);
            }else if(value.equals("NP")){
                if(t.children().length==1){
                    subject = t.children()[0].children()[0].value();
                }else{
                    subject = getNoun(t);
                }
            }
            if(subject != ""){
                return subject;
            }
        }
        for(Tree t: children){
            return BFSforNPSubTrees(t);
        }
        return "";
    }

    /*
    TODO:
    1. currently cannot solve EX like there, one solution is use chain to replace the word
    2. currently cannot solve DT like this, maybe can use chain to fix it as well
     */
    public String getSubject(ArrayList<Tree> NPSubTrees){
        String subject = "";
        for(Tree t: NPSubTrees){
            subject = BFSforNPSubTrees(t);
            if(subject != ""){
                return subject;
            }
        }
        return "";
    }

    public ArrayList<Verb> BFSforVPSubTrees(Tree tree, ArrayList<Verb> verbs){
        Tree[] children = tree.children();
        for(Tree t: children){
            boolean isAdverbPhrase = t.value().equals("ADVP");
            if(isVerb(t.value())){
                Verb verb = new Verb(t.value(),t.children()[0].value());
                verbs.add(verb);
            }else if(isAdverbPhrase){
                for(int i=0;i<t.children().length;i++){
                    if(isAdv(t.children()[i].value())){
                        Verb verb = new Verb(t.children()[i].value(), t.children()[i].children()[0].value());
                        verbs.add(verb);
                    }
                }
            }
        }
        for(Tree t: children){
            BFSforVPSubTrees(t, verbs);
        }
        return verbs;
    }

    private boolean isVerb(String s){
        return s.equals("VB") || s.equals("VBD")
                || s.equals("VBG") || s.equals("VBN")
                || s.equals("VBP")|| s.equals("VBZ");
    }

    private boolean isAdv(String s){
        return s.equals("RB") || s.equals("RBR")
                || s.equals("RBS");
    }

    private boolean isBe(String s){
        return s.equals("is") || s.equals("am") || s.equals("are") || s.equals("were")
                || s.equals("was") || s.equals("been");
    }

    private boolean isPast(String s){
        return s.equals("VBD") || s.equals("VBN");
    }

    private boolean isHave(String s){
        return s.equals("has") || s.equals("have");
    }
    //select the first word a relation maybe
    //1. be + adv + verb
    //2. be + the past participle
    //3. has or have + the past participle
    //4. could / may/ should / might/ would / must  +  have + VBN
    private String relationGenerator(ArrayList<Verb> candidateRelation){
        String relation = candidateRelation.get(0).token;
        if(isBe(candidateRelation.get(0).token) && isAdv(candidateRelation.get(1).type)
                && isVerb(candidateRelation.get(2).type)){
            relation += " " + candidateRelation.get(1).token + " " + candidateRelation.get(2).token;
        }else if(isBe(candidateRelation.get(0).token) && isPast(candidateRelation.get(1).type)){
            relation += " " + candidateRelation.get(1).token;
        }else if(isHave(candidateRelation.get(0).token) && isPast(candidateRelation.get(1).type)){
            relation += " " + candidateRelation.get(1).token;
        }
        return relation;
    }

    //BFS all verbs in VP subtree.
    //After that, generate a relation based on the verbs
    /*TODO:
        1. Maybe can use DFS to search the verbs
        2. Use the location of subject to help identify the relation
     */
    public String findRelation(Tree VPSubtree){
        String relation = "";
        ArrayList<Verb> verbs = new ArrayList<Verb>();
        ArrayList<Verb> candidateRelation = new ArrayList<Verb>();
        boolean hasAddedAdv = false;
        BFSforVPSubTrees(VPSubtree, verbs);
        for(int i=0; i<verbs.size(); i++){
            if(isVerb(verbs.get(i).type)){
                candidateRelation.add(verbs.get(i));
            }else if(isAdv(verbs.get(i).type)){
                hasAddedAdv = true;
                candidateRelation.add(verbs.get(i));
            }else if(!isVerb(verbs.get(i).type)){
                if(hasAddedAdv){
                    ListIterator<Verb> listIter = verbs.listIterator(verbs.size());
                    while (listIter.hasPrevious()) {
                        if (isAdv(listIter.previous().type)) {
                            listIter.remove();
                        }else{
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if(candidateRelation.size()>0){
            relation = relationGenerator(candidateRelation);
        }
        return relation;
    }


    public Triple extractTriplet(Tree sentence){
        Triple triple = new Triple();
        Tree[] children = sentence.children();
        ArrayList<Tree> NPSubTrees = new ArrayList<Tree>();
        ArrayList<Tree> VPSubTrees = new ArrayList<Tree>();
        for(Tree t : children){
            String value = t.value();
            if(value.equals("S")|| value.equals("ROOT")){
                return extractTriplet(t);
            }else if(value.equals("NP")){
                NPSubTrees.add(t);
            }else if(value.equals("VP")){
                VPSubTrees.add(t);
            }
        }
        if(NPSubTrees.size() == 0){
            NPSubTrees.addAll(VPSubTrees);
            if(VPSubTrees.size()==0){

            }
        }
        if(VPSubTrees.size()==0){

        }
        triple.subject = getSubject(NPSubTrees);
        System.out.println("subject: " + triple.subject);
        if(VPSubTrees.size()>0){
            for(Tree t:VPSubTrees){
                System.out.println("input: " + t);
                triple.relation = findRelation(t);
                if(triple.relation != ""){
                    break;
                }
            }
        }
        System.out.println("verb: " + triple.relation);

        return triple;
    }
}
