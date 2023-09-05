package expression;

import expression.evaluator.Evaluator;

public class Const<T> implements GenericExpression<T> {

    private final String value;

    public Const(String value) {
        this.value = value;
    }

    @Override
    public T evaluate(T x, T y, T z, Evaluator<T> evaluator) {
        return evaluator.parseNumber(value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != Const.class || this.getClass().getGenericSuperclass() != obj.getClass().getGenericSuperclass()) {
            return false;
        }
        Const<T> c = (Const<T>) obj;
        return this.value.equals(c.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
