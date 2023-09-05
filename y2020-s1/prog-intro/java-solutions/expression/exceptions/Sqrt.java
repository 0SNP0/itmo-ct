package expression.exceptions;

import expression.PExpression;
import expression.UnaryOperation;

public class Sqrt extends UnaryOperation {

    public Sqrt(PExpression operand) {
        super(operand);
    }

    @Override
    public int action(int x) {
        if (x < 0) {
            throw new NotRealException("sqrt " + x);
        }
        return (int) Math.sqrt(x);
    }

    @Override
    public String operator() {
        return "sqrt ";
    }
}
