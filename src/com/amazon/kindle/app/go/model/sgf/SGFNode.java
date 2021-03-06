package com.amazon.kindle.app.go.model.sgf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SGFNode {

    List properties = new ArrayList();
    
    private Set captures;
    private String previousComment;
    private int[] previousMove;

    void addProperty(SGFProperty property) {
        properties.add(property);
    }

    public List getProperties() {
        return properties;
    }

    static SGFNode fromString(StringBuffer sgf) throws IncorrectFormatException {
        SGFNode node = new SGFNode();
        if (!(sgf.charAt(0) == ';')) {
            throw new IncorrectFormatException();
        }
        sgf.deleteCharAt(0);

        /* Remove leading whitespace */
        while (Character.isWhitespace(sgf.charAt(0))) {
            sgf.deleteCharAt(0);
        }

        while (Character.isUpperCase(sgf.charAt(0))) {
            node.addProperty(SGFProperty.fromString(sgf));

            /* Remove leading whitespace */
            while (Character.isWhitespace(sgf.charAt(0))) {
                sgf.deleteCharAt(0);
            }
        }

        return node;
    }
    
    public void setCaptures(Set captures) {
        this.captures = captures;
    }
    
    public Set getCaptures() {
        return captures;
    }
    
    public void setPreviousComment(String previousComment) {
        this.previousComment = previousComment;
    }
    
    public String getPreviousComment() {
        return previousComment;
    }

    public void setPreviousMove(int[] point) {
        previousMove = point;
    }
    
    public int[] getPreviousMove() {
        return previousMove;
    }
    
    public String toString() {
        String result = ";";
        Iterator i = properties.iterator();
        while (i.hasNext()) {
            result += i.next().toString();
        }
        return result;
    }

}
