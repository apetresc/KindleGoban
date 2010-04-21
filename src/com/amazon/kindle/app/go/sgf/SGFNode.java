package com.amazon.kindle.app.go.sgf;

import java.util.ArrayList;
import java.util.Iterator;

public class SGFNode {

	ArrayList properties = new ArrayList();

	void addProperty(SGFProperty property) {
		properties.add(property);
	}
	
	public ArrayList getProperties() {
		return properties;
	}

	static SGFNode fromString(StringBuffer sgf) throws IncorrectFormatException {
		SGFNode node = new SGFNode();
		if (!(sgf.charAt(0) == ';')) {
			throw new IncorrectFormatException();
		}
		sgf.deleteCharAt(0);

		/* Remove leading whitespace */
		while (Character.isWhitespace(sgf.charAt(0))) {
			sgf.deleteCharAt(0);
		}

		while (Character.isUpperCase(sgf.charAt(0))) {
			node.addProperty(SGFProperty.fromString(sgf));

			/* Remove leading whitespace */
			while (Character.isWhitespace(sgf.charAt(0))) {
				sgf.deleteCharAt(0);
			}
		}

		return node;
	}

	public String toString() {
		String result = ";";
		Iterator i = properties.iterator();
		while (i.hasNext()) {
			result += i.next().toString();
		}
		return result;
	}

}
