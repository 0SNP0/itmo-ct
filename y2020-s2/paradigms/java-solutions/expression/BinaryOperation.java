package expression;

import expression.evaluator.Evaluator;
import expression.exceptions.CalcException;

import java.util.Objects;

public abstract class BinaryOperation<T> implements GenericExpression<T> {

    protected final GenericExpression<T> left, right;

    public BinaryOperation(GenericExpression<T> left, GenericExpression<T> right) {
        this.left = left;
        this.right = right;
    }

    protected abstract T action(T a, T b, Evaluator<T> evaluator) throws CalcException;
    protected abstract String operator();

    @Override
    public T evaluate(T x, T y, T z, Evaluator<T> evaluator) throws CalcException {
        return action(left.evaluate(x, y, z, evaluator), right.evaluate(x, y, z, evaluator), evaluator);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        BinaryOperation<T> expression = (BinaryOperation<T>) obj;
        return this.left.equals(expression.left) && this.right.equals(expression.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, this.getClass());
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator() + " " + right + ")";
    }
}
