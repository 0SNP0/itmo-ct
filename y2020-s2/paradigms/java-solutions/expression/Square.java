package expression;

import expression.evaluator.Evaluator;
import expression.exceptions.CalcException;

public final class Square<T> extends UnaryOperation<T> {

    public Square(GenericExpression<T> operand) {
        super(operand);
    }

    @Override
    protected T action(T a, Evaluator<T> evaluator) throws CalcException {
        return evaluator.square(a);
    }

    @Override
    public String operator() {
        return "square ";
    }
}
