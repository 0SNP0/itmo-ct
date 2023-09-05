package expression.exceptions;

public class ConstantOverflowException extends ParseException {

    public ConstantOverflowException(String message) {
        super("Constant overflow: " + message);
    }
}
