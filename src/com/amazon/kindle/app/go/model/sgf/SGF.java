package com.amazon.kindle.app.go.model.sgf;

import java.io.BufferedReader;
import java.io.IOException;

public class SGF {

    private SGFGameTree gameTree;

    public void parseSGF(String sgf) throws IncorrectFormatException {
        StringBuffer sgfBuf = new StringBuffer(sgf);
        this.gameTree = SGFGameTree.fromString(sgfBuf);
    }

    public void parseSGF(BufferedReader in) throws IncorrectFormatException {
        String sgfString = "";
        String inLine;
        try {
            inLine = in.readLine();

            while (inLine != null) {
                sgfString += inLine;
                inLine = in.readLine();
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        parseSGF(sgfString);
    }

    public SGFIterator iterator() {
        return new SGFIterator(this.gameTree);
    }

    public SGFGameTree getRootTree() {
        return gameTree;
    }

    public String toString() {
        return gameTree.toString();
    }

}
