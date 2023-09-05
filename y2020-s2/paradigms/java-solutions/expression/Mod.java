package expression;

import expression.evaluator.Evaluator;
import expression.exceptions.DivisionByZeroException;

public final class Mod<T> extends BinaryOperation<T> {

    public Mod(GenericExpression<T> left, GenericExpression<T> right) {
        super(left, right);
    }

    @Override
    protected T action(T a, T b, Evaluator<T> evaluator) throws DivisionByZeroException {
        return evaluator.mod(a, b);
    }

    @Override
    protected String operator() {
        return "mod ";
    }
}
