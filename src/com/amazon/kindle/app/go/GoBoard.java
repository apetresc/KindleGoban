package com.amazon.kindle.app.go;

public class GoBoard {

	private int size;
	private int[][] board;
	
	public static final int OFF_BOARD = -1;
	public static final int BLANK = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	
	public GoBoard(int size) {
		this.size = size;
		this.board = new int[size][size];
	}
	
	public void init() {
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
	
	public int getSize() {
		return this.size;
	}
}
