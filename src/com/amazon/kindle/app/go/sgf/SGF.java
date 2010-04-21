package com.amazon.kindle.app.go.sgf;

public class SGF {

    private SGFGameTree gameTree;
    
    public void parseSGF (String sgf) throws IncorrectFormatException {
            StringBuffer sgfBuf = new StringBuffer(sgf);
            this.gameTree = SGFGameTree.fromString(sgfBuf);
    }
    
    public SGFGameTree getRootTree() {
    	return gameTree;
    }
    
    public String toString() {
            return gameTree.toString();
    }
    
}
