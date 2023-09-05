package expression.exceptions;

import expression.PExpression;
import expression.UnaryOperation;

public class Abs extends UnaryOperation {

    public Abs(PExpression operand) {
        super(operand);
    }

    @Override
    public int action(int x) {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException("abs -(2^31)");
        }
        return x < 0 ? -x : x;
    }

    @Override
    public String operator() {
        return "abs ";
    }
}
