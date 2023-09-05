import java.io.*;
import java.nio.charset.StandardCharsets;

public class Scanner implements AutoCloseable {
    private final BufferedReader in;
    private String token;
    private int next = -1;
    private int lscount = 0;
    private int nextint;

    public interface Checker {
        boolean check(char c);
    }

    Scanner(InputStream input) throws IOException {
        in = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

    Scanner(File file) throws IOException {
        this(new FileInputStream(file));
    }

//    Scanner(String s) throws IOException {
//        in = new BufferedReader(new StringReader(s));
//    }

//    private boolean hasNext() {
//        return next != -1;
//    }

    private void nextToken(Checker checker) throws IOException {
        if (next == -1) {
            next = in.read();
        }
        if (token == null) {
            StringBuilder buf = new StringBuilder();
            for (char p = 0; next != -1 && !checker.check((char) next); next = in.read()) {
                if (System.lineSeparator().contains(String.valueOf((char) next))) {
                    if (p != '\r') {
                        lscount++;
                    }
                }
                p = (char) next;
            }
            for (; next != -1 && checker.check((char) next); next = in.read()) {
                buf.append((char) next);
            }
            if (buf.length() == 0) {
                token = null;
            } else {
                token = new String(buf);
            }
        }
    }

    public boolean hasNext(Checker checker) throws IOException {
        nextToken(checker);
        return token != null;
    }

    public String next(Checker checker) throws IOException {
        nextToken(checker);
        String res = token;
        token = null;
        return res;
    }

    public boolean hasNextInt() throws IOException {
        try {
            nextToken(Scanner::intChecker);
            token = token.toLowerCase();
            if (token.length() > 2 && token.charAt(0) == '0' && token.charAt(1) == 'x') {
                nextint = (int) Long.parseLong(token.substring(2, token.length()), 16);
            } else {
                nextint = Integer.parseInt(token);
            }
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    public int nextInt() throws IOException {
        if (hasNextInt()) {
            token = null;
            return nextint;
        } else {
            return 0;
        }
    }

    public int countOfLineSep() throws IOException {
        nextToken(Scanner::intChecker);
        int res = lscount;
        lscount = 0;
        return res;
    }

    public int countOfLineSep(Checker checker) throws IOException {
        nextToken(checker);
        int res = lscount;
        lscount = 0;
        return res;
    }

    public boolean isNotEnd() throws IOException {
        if (next == -1) {
            next = in.read();
        }
        return next != -1 || hasNextInt();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    private static boolean intChecker(char c) {
        return Character.isDigit(c) || Character.isLetter(c) || c == '-';
    }
}
