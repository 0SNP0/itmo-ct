package expression.exceptions;

import expression.Multiply;
import expression.PExpression;

public class CheckedMultiply extends Multiply {

    public CheckedMultiply(PExpression operand1, PExpression operand2) {
        super(operand1, operand2);
    }

    @Override
    protected int action(int operand1, int operand2) {
        final int res = operand1 * operand2;
        if (operand1 > 0 && operand2 > 0 && Integer.MAX_VALUE / operand1 < operand2 ||
                operand1 < 0 && operand2 < 0 && Integer.MAX_VALUE / operand1 > operand2 ||
                operand1 > 0 && operand2 < 0 && Integer.MIN_VALUE / operand1 > operand2 ||
                operand1 != -1 && operand1 < 0 && operand2 > 0 && Integer.MIN_VALUE / operand1 < operand2) {
            throw new OverflowException(operand1 + " * " + operand2);
        }
        return super.action(operand1, operand2);
    }
}
