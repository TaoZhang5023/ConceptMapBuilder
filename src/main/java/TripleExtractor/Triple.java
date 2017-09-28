package TripleExtractor;

import java.util.ArrayList;

/**
 * Created by zhangtao on 28/9/17.
 */
public class Triple {
    String subject;
    String relation;
    ArrayList<String> objects;

    public Triple(){
        this.subject = "";
        this.relation = "";
        this.objects = new ArrayList<String>();
    }
}
