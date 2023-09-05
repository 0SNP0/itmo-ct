package expression.exceptions;

import expression.Divide;
import expression.PExpression;

public class CheckedDivide extends Divide {

    public CheckedDivide(PExpression operand1, PExpression operand2) {
        super(operand1, operand2);
    }

    @Override
    protected int action(int operand1, int operand2) {
        if (operand2 == 0) {
            throw new DivisionByZeroException(operand1);
        }
        if (operand1 == Integer.MIN_VALUE && operand2 == -1) {
            throw new OverflowException("-(2^31) / -1");
        }
        return super.action(operand1, operand2);
    }
}
