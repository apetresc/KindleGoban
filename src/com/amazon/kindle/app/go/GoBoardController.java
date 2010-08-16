package com.amazon.kindle.app.go;

import java.io.BufferedReader;
import java.util.Set;

import com.amazon.kindle.app.go.model.sgf.IncorrectFormatException;
import com.amazon.kindle.app.go.model.sgf.SGF;
import com.amazon.kindle.app.go.model.sgf.SGFIterator;

public class GoBoardController {

    private GoBoard     board;
    private SGF         sgf;
    private SGFIterator sgfIterator;
    
    private int currentMoveNumber;
    
    public GoBoardController(GoBoard board) {
        this.board = board;
    }
    
    public void loadSGF(BufferedReader in) throws IncorrectFormatException {
        sgf = new SGF();
        sgf.parseSGF(in);
        sgfIterator = sgf.iterator();
        currentMoveNumber = 0;
    }
    
    public Set nextMove() {
        if (sgfIterator == null || !sgfIterator.hasNext()) {
            return null;
        }
        Set affectedPoints = board.applyNode(sgfIterator.next());
        currentMoveNumber++;
        return affectedPoints;
    }
    
    public boolean previousMove() {
        if (sgfIterator == null || !sgfIterator.hasPrevious()) {
            return false;
        }
        board.rewindNode(sgfIterator.previous());
        currentMoveNumber--;
        return true;
    }
    
    public boolean goToMove(int move) {
        while (currentMoveNumber < move && (nextMove() != null)) { }
        return currentMoveNumber == move;
    }
    
}
