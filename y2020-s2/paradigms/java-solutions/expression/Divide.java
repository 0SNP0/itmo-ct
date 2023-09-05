package expression;

import expression.evaluator.Evaluator;
import expression.exceptions.CalcException;

public final class Divide<T> extends BinaryOperation<T> {

    public Divide(GenericExpression<T> left, GenericExpression<T> right) {
        super(left, right);
    }

    @Override
    protected T action(T a, T b, Evaluator<T> evaluator) throws CalcException {
        return evaluator.divide(a, b);
    }

    @Override
    protected String operator() {
        return "/";
    }
}
