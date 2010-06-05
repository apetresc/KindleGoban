package com.amazon.kindle.app.go.model.sgf;

import java.util.List;

public class SGFProperty {

    String propIdent;
    String propValue;
    List propValues;

    void addValue(String value) {
        if (propValue == null && propValues == null) {
            propValue = value;
            return;
        }
        else {
            propValue = null;
            propValues.add(value);
        }
    }

    public String[] getValues() {
        if (propValues == null) {
            return new String[] { propValue };
        } else {
            return (String[]) propValues.toArray(new String[0]);
        }
    }

    public String getIdent() {
        return propIdent;
    }


    public SGFProperty(String propIdent) {
        this.propIdent = propIdent;
    }

    private String escapePropertyValue(String unescapedString) {
        String escapedString = "";
        for (int i = 0; i < unescapedString.length(); i++) {
            char c = unescapedString.charAt(i);
            switch (c) {
            case ']':
                escapedString += '\\';
            default:
                escapedString += c;
            } 
        }
        return escapedString;
    }

    static SGFProperty fromString(StringBuffer sgf)
    throws IncorrectFormatException {

        /* Remove leading whitespace */
        if (!Character.isUpperCase(sgf.charAt(0))) {
            throw new IncorrectFormatException();
        }

        String propIdent = String.valueOf(sgf.charAt(0));
        sgf.deleteCharAt(0);
        while (Character.isUpperCase(sgf.charAt(0))) {
            propIdent += String.valueOf(sgf.charAt(0));
            sgf.deleteCharAt(0);
        }

        if (!(sgf.charAt(0) == '[')) {
            throw new IncorrectFormatException();
        }

        SGFProperty property = new SGFProperty(propIdent);


        while (sgf.charAt(0) == '[') {
            sgf.deleteCharAt(0);
            String propValue = "";
            boolean escape = false;
            while (!escape && !(sgf.charAt(0) == ']')) {
                escape = false;
                char nextChar = sgf.charAt(0);
                sgf.deleteCharAt(0);

                if (nextChar == '\\') {
                    escape = true;
                } else {
                    propValue += String.valueOf(nextChar);
                }
            }
            sgf.deleteCharAt(0);
            property.addValue(propValue);

            /* Remove leading whitespace */
            while (Character.isWhitespace(sgf.charAt(0))) {
                sgf.deleteCharAt(0);
            }
        }

        return property;
    }

    public String toString() {
        String result = propIdent;
        String[] values = getValues();
        for (int i = 0; i < values.length; i++) {
            result += "[" + escapePropertyValue(values[i]) + "]";
        }
        return result;
    }

}
