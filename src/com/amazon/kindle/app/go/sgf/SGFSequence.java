package com.amazon.kindle.app.go.sgf;

import java.util.ArrayList;
import java.util.Iterator;

public class SGFSequence {

	private ArrayList nodes = new ArrayList();

	void addNode(SGFNode node) {
		nodes.add(node);
	}
	
	public ArrayList getNodes() {
		return nodes;
	}

	static SGFSequence fromString(StringBuffer sgf) throws IncorrectFormatException {
		SGFSequence sequence = new SGFSequence();
		sequence.addNode(SGFNode.fromString(sgf));

		/* Remove leading whitespace */
		while (Character.isWhitespace(sgf.charAt(0))) {
			sgf.deleteCharAt(0);
		}

		while (sgf.charAt(0) == ';') {
			sequence.addNode(SGFNode.fromString(sgf));

			/* Remove leading whitespace */
			while (Character.isWhitespace(sgf.charAt(0))) {
				sgf.deleteCharAt(0);
			}
		}

		return sequence;
	}

	public String toString() {
		String result = "";
		Iterator i = nodes.iterator();
		while (i.hasNext()) {
			result += i.next().toString();
		}
		return result;
	}

}
