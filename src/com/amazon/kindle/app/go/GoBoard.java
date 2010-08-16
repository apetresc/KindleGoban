package com.amazon.kindle.app.go;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.amazon.kindle.app.go.model.sgf.SGFNode;
import com.amazon.kindle.app.go.model.sgf.SGFProperty;

public class GoBoard {

    private int size;
    private int[][] board;
    private int[][][] markup;
    private String comment;

    public static final int OFF_BOARD = -1;
    public static final int BLANK = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    // MARKUP
    private static final int MARKUP  = 10;
    public static final int CIRCLE   = MARKUP + 1;
    public static final int TRIANGLE = MARKUP + 2;
    public static final int SQUARE   = MARKUP + 3;

    public GoBoard(int size) {
        this.size = size;
    }

    public void init() {
        this.board = new int[size][size];
    }

    private void initMarkup() {
        this.markup = new int[size][size][];
    }

    public static int[] convertSGFToCoordinates(String sgfCoordinate) {
        char x = sgfCoordinate.charAt(0);
        char y = sgfCoordinate.charAt(1);

        return new int[] { x - 'a', y - 'a' };
    }
    
    public String getComment() {
        return (comment == null) ? "" : comment;
    }

    public int getPoint(int x, int y) {
        if (x >= size || x < 0 || y >= size || y < 0)
            return OFF_BOARD;
        return board[x][y];
    }

    public int setPoint(int color, int x, int y) {
        if (x >= size || x < 0 || y >= size || y < 0)
            return OFF_BOARD;
        if (color != BLANK && color != BLACK && color != WHITE)
            return OFF_BOARD;
        board[x][y] = color;
        return color;
    }

    public void setMarkup(int[][][] newMarkup) {
        if (this.markup == null) initMarkup();

    }

    public int getSize() {
        return this.size;
    }

    public Set applyNode(SGFNode node) {
        node.setPreviousComment(comment);
        comment = null;
        Set capturedStones = null;
        int[] point = null;
        
        List properties = node.getProperties();
        Iterator it = properties.iterator();
        while (it.hasNext()) {
            SGFProperty property = (SGFProperty) it.next();
            if (property.getIdent().equals("B") || property.getIdent().equals("W")) {
                point = convertSGFToCoordinates(property.getValues()[0]);
                
                this.setPoint(property.getIdent().equals("B") ? BLACK : WHITE, point[0], point[1]);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if ((dx+dy != 1) && (dx+dy != -1)) continue;
                        if (getPoint(point[0]+dx, point[1]+dy) == (property.getIdent().equals("B") ? WHITE : BLACK)) {
                            Set group = floodFill(point[0]+dx, point[1]+dy);
                            if (!hasLiberties(group)) {
                                if (capturedStones == null) capturedStones = new HashSet();
                                Iterator groupIterator = group.iterator();
                                while (groupIterator.hasNext()) {
                                    int[] groupPoint = (int[]) groupIterator.next();
                                    setPoint(BLANK, groupPoint[0], groupPoint[1]);
                                    capturedStones.add(groupPoint);
                                }
                                node.setCaptures(capturedStones);
                            }
                        }
                    }
                }
            } else if (property.getIdent().equals("C")) {
                comment = property.getValues()[0];
            }
        }
        
        if (point == null) {
            return null;
        } else if (point != null && capturedStones == null) {
            capturedStones = new HashSet(1);
            capturedStones.add(point);
            return capturedStones;
        } else if (point != null && capturedStones != null) {
            Set affectedStones = new HashSet(capturedStones);
            affectedStones.add(point);
            return affectedStones;
        }
        
        return null;
    }
    
    public void rewindNode(SGFNode node) {
        comment = node.getPreviousComment();
        List properties = node.getProperties();
        Iterator it = properties.iterator();
        while (it.hasNext()) {
            SGFProperty property = (SGFProperty) it.next();
            if (property.getIdent().equals("B") || property.getIdent().equals("W")) {
                int[] point = convertSGFToCoordinates(property.getValues()[0]);
                
                this.setPoint(BLANK, point[0], point[1]);
                if (node.getCaptures() != null) {
                    Set captures = node.getCaptures();
                    Iterator captureIterator = captures.iterator();
                    while (captureIterator.hasNext()) {
                        int[] capturePoint = (int[]) captureIterator.next();
                        this.setPoint(property.getIdent().equals("B") ? WHITE : BLACK, capturePoint[0], capturePoint[1]);
                    }
                }
            }
        }
    }

    private Set floodFill(int x, int y) {
        Set group = new HashSet();
        LinkedList points = new LinkedList();
        points.add(new int[] {x, y});
        while (!points.isEmpty()) {
            int[] point = (int[]) points.removeFirst();
            group.add(point);
            for (int dx = -1; dx <= 1; dx++) {
                inner:
                    for (int dy = -1; dy <= 1; dy++) {
                        if ((dx+dy != 1) && (dx+dy != -1)) continue;
                        if (getPoint(point[0]+dx, point[1]+dy) != getPoint(point[0], point[1])) continue;

                        Iterator it = group.iterator();
                        while (it.hasNext()) {
                            int[] p = (int[]) it.next();
                            if ((p[0] == point[0] + dx) && (p[1] == point[1] + dy)) {
                                continue inner;
                            }
                        }

                        points.addLast(new int[] {point[0]+dx, point[1]+dy}); 
                    }
            }
        }
        return group;
    }
    
    private boolean hasLiberties(Set points) {
        Iterator it = points.iterator();
        while (it.hasNext()) {
            int[] point = (int[]) it.next();
            for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if ((dx+dy != 1) && (dx+dy != -1)) continue;
                        if (getPoint(point[0]+dx, point[1]+dy) != BLANK) continue;
                        
                        return true;
                    }
            }
        }
        
        return false;
    }
}
