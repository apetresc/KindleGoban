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
		board[0][0] = WHITE;
		board[18][18] = BLACK;
		board[18][9] = WHITE;
		board[3][4] = WHITE;
		board[3][3] = BLACK;
		board[4][3] = WHITE;
		board[2][3] = WHITE;
		board[3][2] = WHITE;
		board[16][2] = WHITE;
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
