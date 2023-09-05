package expression.exceptions;

public class UnknownLexemeException extends ParseException {
    public UnknownLexemeException(String message) {
        super("Unexpected lexeme: " + message);
    }
}
