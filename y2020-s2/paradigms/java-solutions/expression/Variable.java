package expression;

import expression.evaluator.Evaluator;
import expression.exceptions.CalcException;

public class Variable<T> implements GenericExpression<T> {

    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public T evaluate(T x, T y, T z, Evaluator<T> evaluator) {
        switch (name) {
            case "x": return x;
            case "y": return y;
            case "z": return z;
            default: throw new CalcException("Incorrect variable");
        }
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != Variable.class) {
            return false;
        }
        return name.equals(obj.toString());
    }

    @Override
    public String toString() {
        return this.name;
    }
}
