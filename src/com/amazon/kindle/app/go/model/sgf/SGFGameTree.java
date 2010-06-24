package com.amazon.kindle.app.go.model.sgf;

import java.util.ArrayList;

public class SGFGameTree {

    private SGFSequence sequence;
    private SGFGameTree parent;
    private SGFGameTree[] subtrees;

    public SGFSequence getSequence() {
        return sequence;
    }
    
    public SGFGameTree getParent() {
        return parent;
    }

    public SGFGameTree[] getSubtrees() {
        return subtrees;
    }
    
    public SGFGameTree(SGFGameTree parent) {
        this.parent = parent;
    }

    static SGFGameTree fromString(StringBuffer sgf, SGFGameTree parent) throws IncorrectFormatException {
        SGFGameTree gameTree = new SGFGameTree(parent);
        ArrayList subtrees = new ArrayList();

        /* Remove leading whitespace */
        while (Character.isWhitespace(sgf.charAt(0))) {
            sgf.deleteCharAt(0);
        }

        if (!(sgf.charAt(0) == '(')) {
            throw new IncorrectFormatException();
        }
        sgf.deleteCharAt(0);

        /* Remove leading whitespace */
        while (Character.isWhitespace(sgf.charAt(0))) {
            sgf.deleteCharAt(0);
        }

        gameTree.sequence = SGFSequence.fromString(sgf);

        /* Remove leading whitespace */
        while (Character.isWhitespace(sgf.charAt(0))) {
            sgf.deleteCharAt(0);
        }

        while (!(sgf.charAt(0) == ')')) {
            subtrees.add(SGFGameTree.fromString(sgf, gameTree));
        }
        gameTree.subtrees = (SGFGameTree[]) subtrees.toArray(new SGFGameTree[0]);

        sgf.deleteCharAt(0);

        return gameTree;
    }

    public String toString() {
        String result = "(";
        result += sequence.toString();
        for (int i = 0; i < subtrees.length; i++) {
            result += subtrees[i].toString();
        }
        result += ")";
        return result;
    }
}
