package expression;

import expression.evaluator.Evaluator;
import expression.exceptions.OverflowException;

public final class Add<T> extends BinaryOperation<T> {

    public Add(GenericExpression<T> left, GenericExpression<T> right) {
        super(left, right);
    }

    @Override
    protected T action(T a, T b, Evaluator<T> evaluator) throws OverflowException {
        return evaluator.add(a, b);
    }

    @Override
    protected String operator() {
        return "+";
    }
}
