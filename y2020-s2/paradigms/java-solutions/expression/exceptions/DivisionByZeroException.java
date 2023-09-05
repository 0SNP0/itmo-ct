package expression.exceptions;

public class DivisionByZeroException extends CalcException {
    public DivisionByZeroException(Object o1) {
        super("division by zero: " + o1 + "/0");
    }
}