package com.amazon.kindle.app.go.model.sgf;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;

public class SGFSequence {

    private List nodes = new LinkedList();

    void addNode(SGFNode node) {
        nodes.add(node);
    }

    public List getNodes() {
        return nodes;
    }

    static SGFSequence fromString(StringBuffer sgf) throws IncorrectFormatException {
        SGFSequence sequence = new SGFSequence();
        sequence.addNode(SGFNode.fromString(sgf));

        /* Remove leading whitespace */
        while (Character.isWhitespace(sgf.charAt(0))) {
            sgf.deleteCharAt(0);
        }

        while (sgf.charAt(0) == ';') {
            sequence.addNode(SGFNode.fromString(sgf));

            /* Remove leading whitespace */
            while (Character.isWhitespace(sgf.charAt(0))) {
                sgf.deleteCharAt(0);
            }
        }

        return sequence;
    }

    public String toString() {
        String result = "";
        Iterator i = nodes.iterator();
        while (i.hasNext()) {
            result += i.next().toString();
        }
        return result;
    }

}
