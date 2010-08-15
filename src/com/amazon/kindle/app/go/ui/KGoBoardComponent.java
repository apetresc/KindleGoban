package com.amazon.kindle.app.go.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.amazon.kindle.app.go.GoBoard;
import com.amazon.kindle.kindlet.ui.KComponent;

public class KGoBoardComponent extends KComponent {
    private static final long serialVersionUID = -411749555428258146L;

    private static final int SQUARE_SIZE = 40;
    private static final int STONE_SIZE  = SQUARE_SIZE;
    private static final int STAR_SIZE = STONE_SIZE/5;
    private static final int GLOBAL_X_OFFSET = 0;
    private static final int GLOBAL_Y_OFFSET = 0;
    private static final int BORDER_WIDTH = 4;
    
    private GoBoard board;
    private Set affectedSquares;
    
    public KGoBoardComponent(GoBoard board) {
        super();
        this.board = board;
        this.affectedSquares = new HashSet();
    }
    
    public void setAffectedSquares(Set affectedSquares) {
        this.affectedSquares.addAll(affectedSquares);
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(board.getSize() * SQUARE_SIZE + STONE_SIZE, board.getSize() * SQUARE_SIZE + STONE_SIZE);
    }
    
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }
    
    public Dimension getMaximumSize() {
        return this.getPreferredSize();
    }
    
    public void paint(Graphics g) {
        if (affectedSquares.isEmpty() || true) {
            paintWholeBoard(g);
        } else {
            paintAffected(g);
            affectedSquares.clear();
        }
    }
    
    private void paintAffected(Graphics g) {
        final int X_OFFSET = STONE_SIZE/2 + GLOBAL_X_OFFSET;
        final int Y_OFFSET = STONE_SIZE/2 + GLOBAL_Y_OFFSET;
        
        Iterator it = affectedSquares.iterator();
        while (it.hasNext()) {
            int[] point = (int[]) it.next();
            int x = point[0];
            int y = point[1];
            
            switch (board.getPoint(x, y)) {
            case GoBoard.BLACK:
                g.setColor(Color.BLACK);
                g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2 - 1,
                        Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2 - 1, 
                        STONE_SIZE+2,
                        STONE_SIZE+2);
                break;
            case GoBoard.WHITE:
                g.setColor(Color.BLACK);
                g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2,
                        Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2,
                        STONE_SIZE+1,
                        STONE_SIZE+1);
                g.setColor(Color.WHITE);
                g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2 + BORDER_WIDTH/2,
                        Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2 + BORDER_WIDTH/2,
                        STONE_SIZE - BORDER_WIDTH,
                        STONE_SIZE - BORDER_WIDTH);
                break;
            case GoBoard.BLANK:
                break;
            }
        }
    }
    
    private void paintWholeBoard(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 
                0, 
                board.getSize() * SQUARE_SIZE + STONE_SIZE + GLOBAL_X_OFFSET, 
                board.getSize() * SQUARE_SIZE + STONE_SIZE + GLOBAL_Y_OFFSET);

        final int X_OFFSET = STONE_SIZE/2 + GLOBAL_X_OFFSET;
        final int Y_OFFSET = STONE_SIZE/2 + GLOBAL_Y_OFFSET;

        g.setColor(Color.BLACK);
        // Draw border
        g.fillRect(0 + X_OFFSET, 
                0 + Y_OFFSET,
                BORDER_WIDTH, 
                (board.getSize() - 1) * SQUARE_SIZE);

        g.fillRect(0 + X_OFFSET,
                0 + Y_OFFSET,
                (board.getSize() - 1) * SQUARE_SIZE,
                BORDER_WIDTH);

        g.fillRect(0 + X_OFFSET,
                (board.getSize() - 1) * SQUARE_SIZE + Y_OFFSET,
                (board.getSize() - 1) * SQUARE_SIZE,
                BORDER_WIDTH);

        g.fillRect((board.getSize() - 1) * SQUARE_SIZE + X_OFFSET,
                0 + Y_OFFSET, 
                BORDER_WIDTH, 
                (board.getSize() - 1) * SQUARE_SIZE + BORDER_WIDTH);

        // Draw grid
        for (int x = 0; x < board.getSize(); x++) {
            g.drawLine(0 + X_OFFSET,
                    x * SQUARE_SIZE + Y_OFFSET,
                    (board.getSize() - 1) * SQUARE_SIZE + X_OFFSET,
                    x * SQUARE_SIZE + Y_OFFSET);
            g.drawLine(x * SQUARE_SIZE + X_OFFSET,
                    0 + Y_OFFSET,
                    x * SQUARE_SIZE + X_OFFSET,
                    (board.getSize() - 1) * SQUARE_SIZE + Y_OFFSET);
        }

        // Draw star points
        int[] starPoints = new int[3];
        switch (board.getSize()) {
        case 19:
            starPoints = new int[] {3, 9, 15};
            break;
        default:
            starPoints = new int[0];
        }

        for (int x = 0; x < starPoints.length; x++) {
            for (int y = 0; y < starPoints.length; y++) {
                if (board.getPoint(starPoints[x], starPoints[y]) == GoBoard.BLANK) {
                    g.fillOval(X_OFFSET + starPoints[x] * SQUARE_SIZE - STAR_SIZE/2,
                            Y_OFFSET + starPoints[y] * SQUARE_SIZE - STAR_SIZE/2,
                            STAR_SIZE,
                            STAR_SIZE);
                }
            }
        }

        // Draw the actual stones
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                switch (board.getPoint(x, y)) {
                case GoBoard.BLACK:
                    g.setColor(Color.BLACK);
                    g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2 - 1,
                            Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2 - 1, 
                            STONE_SIZE+2,
                            STONE_SIZE+2);
                    break;
                case GoBoard.WHITE:
                    g.setColor(Color.BLACK);
                    g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2,
                            Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2,
                            STONE_SIZE+1,
                            STONE_SIZE+1);
                    g.setColor(Color.WHITE);
                    g.fillOval(X_OFFSET + x * SQUARE_SIZE - STONE_SIZE/2 + BORDER_WIDTH/2,
                            Y_OFFSET + y * SQUARE_SIZE - STONE_SIZE/2 + BORDER_WIDTH/2,
                            STONE_SIZE - BORDER_WIDTH,
                            STONE_SIZE - BORDER_WIDTH);
                    break;
                case GoBoard.BLANK:
                    break;
                }
            }
        }
    }
    
}
