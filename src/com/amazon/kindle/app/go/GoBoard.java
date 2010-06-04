package com.amazon.kindle.app.go;

import java.util.Iterator;
import java.util.List;

import com.amazon.kindle.app.go.model.sgf.SGFNode;
import com.amazon.kindle.app.go.model.sgf.SGFProperty;

public class GoBoard {

	private int size;
	private int[][] board;
	private int[][][] markup;
	
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
	
	public void applyNode(SGFNode node) {
		List properties = node.getProperties();
		Iterator it = properties.iterator();
		while (it.hasNext()) {
			SGFProperty property = (SGFProperty) it.next();
			if (property.getIdent().equals("B")) {
				int[] point = convertSGFToCoordinates(property.getValues()[0]);
				this.setPoint(BLACK, point[0], point[1]);
			}
			if (property.getIdent().equals("W")) {
				int[] point = convertSGFToCoordinates(property.getValues()[0]);
				this.setPoint(WHITE, point[0], point[1]);
			}
		}
	}
}
