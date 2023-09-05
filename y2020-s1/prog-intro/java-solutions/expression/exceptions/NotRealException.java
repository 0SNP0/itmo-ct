package expression.exceptions;

public class NotRealException extends CalcException {

    public NotRealException(String message) {
        super("Not real number: " + message);
    }
}
