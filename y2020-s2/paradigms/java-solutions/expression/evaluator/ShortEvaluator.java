package expression.evaluator;

import expression.exceptions.DivisionByZeroException;

public class ShortEvaluator implements Evaluator<Short> {

    @Override
    public Short add(Short a, Short b) {
        return (short) (a + b);
    }

    @Override
    public Short subtract(Short a, Short b) {
        return (short) (a - b);
    }

    @Override
    public Short multiply(Short a, Short b) {
        return (short) (a * b);
    }

    @Override
    public Short divide(Short a, Short b) {
        if (b == 0) {
            throw new DivisionByZeroException(b);
        }
        return (short) (a / b);
    }

    @Override
    public Short negate(Short a) {
        return (short) -a;
    }

    @Override
    public Short abs(Short a) {
        return (short) (a < 0 ? -a : a);
    }

    @Override
    public Short mod(Short a, Short b) {
        if (b == 0) {
            throw new DivisionByZeroException(b);
        }
        return (short) (a % b);
    }

    @Override
    public Short parseNumber(String str) {
        return Short.parseShort(str);
    }

    @Override
    public Short castInt(int v) {
        return (short) v;
    }
}