package expression.parser;

import expression.exceptions.ParseException;

public class TokenSource{

    private final String data;
    private int pos;
    private int prevprev;

    public TokenSource(final String data) {
        this.data = data;
    }

    public boolean hasNext() {
        for (; pos < data.length() && Character.isWhitespace(data.charAt(pos)); pos++);
        return pos < data.length();
    }

    public String next() {
        for (; pos < data.length() && Character.isWhitespace(data.charAt(pos)); pos++);
        int i = pos;
        for (; i < data.length() && Character.isLetter(data.charAt(i)); i++);
        for (; i < data.length() && Character.isDigit(data.charAt(i)); i++);
        if (i == pos) {
            i++;
        }
        prevprev = pos - 1;
        return data.substring(pos, pos = i);
    }

    public char prevBack() {
        return data.charAt(prevprev);
    }
}
