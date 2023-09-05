package expression;

import expression.evaluator.Evaluator;
import expression.exceptions.OverflowException;

public final class Negate<T> extends UnaryOperation<T> {

    public Negate(GenericExpression<T> operand) {
        super(operand);
    }

    @Override
    protected T action(T a, Evaluator<T> evaluator) throws OverflowException {
        return evaluator.negate(a);
    }

    @Override
    public String operator() {
        return "-";
    }
}
