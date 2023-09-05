package expression;

import expression.evaluator.Evaluator;
import expression.exceptions.CalcException;

import java.util.Objects;

public abstract class UnaryOperation<T> implements GenericExpression<T> {

    private final GenericExpression<T> operand;

    public UnaryOperation(GenericExpression<T> operand) {
        this.operand = operand;
    }

    protected abstract T action(T a, Evaluator<T> evaluator) throws CalcException;
    public abstract String operator();

    @Override
    public T evaluate(T x, T y, T z, Evaluator<T> evaluator) {
        return action(operand.evaluate(x, y, z, evaluator), evaluator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand, this.getClass());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        UnaryOperation<T> expression = (UnaryOperation<T>) obj;
        return this.operand.equals(expression.operand);
    }

    @Override
    public String toString() {
        return "(" + operator() + operand.toString() + ")";
    }
}
