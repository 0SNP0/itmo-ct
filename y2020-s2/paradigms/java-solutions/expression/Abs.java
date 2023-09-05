package expression;

import expression.evaluator.Evaluator;
import expression.exceptions.OverflowException;

public final class Abs<T> extends UnaryOperation<T> {

    public Abs(GenericExpression<T> operand) {
        super(operand);
    }

    @Override
    protected T action(T a, Evaluator<T> evaluator) throws OverflowException {
        return evaluator.abs(a);
    }

    @Override
    public String operator() {
        return "abs ";
    }
}
