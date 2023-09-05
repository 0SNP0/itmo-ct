package expression;

import expression.evaluator.Evaluator;
import expression.exceptions.OverflowException;

public final class Subtract<T> extends BinaryOperation<T> {

    public Subtract(GenericExpression<T> left, GenericExpression<T> right) {
        super(left, right);
    }

    @Override
    protected T action(T a, T b, Evaluator<T> evaluator) throws OverflowException {
        return evaluator.subtract(a, b);
    }

    @Override
    protected String operator() {
        return "-";
    }
}
