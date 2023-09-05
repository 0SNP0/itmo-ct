package shell;

import shell.units.*;

import static shell.units.Command.c;

public class Parser {

    public static Unit parse(String line) throws ParserException {
        //TODO:
        return c(line);
        // throw new ParserException("The parser hasn't been implemented yet");
    }

    static class ParserException extends Exception {

        public ParserException(String message) {
            super(message);
        }

    }
}
