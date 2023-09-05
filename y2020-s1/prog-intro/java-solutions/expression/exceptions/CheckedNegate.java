package expression.exceptions;

import expression.Negate;
import expression.PExpression;

public class CheckedNegate extends Negate {

    public CheckedNegate(PExpression operand) {
        super(operand);
    }

    @Override
    public int action(int x) {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException("-(2^31)");
        }
        return super.action(x);
    }
}
