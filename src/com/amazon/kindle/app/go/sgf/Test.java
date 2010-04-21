package com.amazon.kindle.app.go.sgf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException,
			IncorrectFormatException {
		BufferedReader in = new BufferedReader(new FileReader("sgf/sgf24.sgf"));
		String sgf = "";
		String inLine = in.readLine();
		while (inLine != null) {
			sgf += inLine + "\n";
			inLine = in.readLine();
		}

		System.out.println(sgf);

		SGFGameTree gameTree = SGFGameTree.fromString(new StringBuffer(sgf));
		int a = 3;
		a++;
		System.out.println("=============================================");
		System.out.println(gameTree.toString());
	}

}
