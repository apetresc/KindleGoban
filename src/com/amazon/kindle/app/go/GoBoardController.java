package com.amazon.kindle.app.go;

import java.io.BufferedReader;

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
    
    public boolean nextMove() {
        if (sgfIterator == null || !sgfIterator.hasNext()) {
            return false;
        }
        if (board.applyNode(sgfIterator.next())) currentMoveNumber++;
        return true;
    }
    
    public boolean previousMove() {
        if (sgfIterator == null || !sgfIterator.hasPrevious()) {
            return false;
        }
        if (board.rewindNode(sgfIterator.previous())) currentMoveNumber--;
        return true;
    }
    
    public boolean goToMove(int move) {
        while (currentMoveNumber < move && nextMove()) { }
        while (currentMoveNumber > move && previousMove()) { }
        return currentMoveNumber == move;
    }
    
    public int getCurrentMoveNumber() {
        return currentMoveNumber;
    }
    
    public int getMainBranchLength() {
        int currentMove = currentMoveNumber;
        while (nextMove());
        int lastMove = currentMoveNumber;
        goToMove(currentMove);
        return lastMove;
    }
}
