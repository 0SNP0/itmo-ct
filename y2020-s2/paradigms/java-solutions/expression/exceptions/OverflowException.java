package expression.exceptions;

public class OverflowException extends CalcException {
    public OverflowException(String message) {
        super("Overflow: " + message);
    }
}
