package expression.exceptions;

public class MissingLexemeException extends ParseException {
    public MissingLexemeException(String message) {
        super("Missing lexeme: " + message);
    }
}
