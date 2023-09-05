package expression.evaluator;

import expression.exceptions.OverflowException;

public class IntegerEvaluator extends UncheckedIntegerEvaluator {

    @Override
    public Integer add(Integer a, Integer b) {
        if (a > 0 && Integer.MAX_VALUE - a < b ||
                a < 0 && Integer.MIN_VALUE - a > b) {
            throw new OverflowException(a + " + " + b);
        }
        return super.add(a, b);
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        if (!(a < 0 && b == Integer.MIN_VALUE) && (b == Integer.MIN_VALUE || a > 0 && Integer.MAX_VALUE - a < -b || a < 0 && Integer.MIN_VALUE - a > -b)) {
            throw new OverflowException(a + " - " + b);
        }
        return super.subtract(a, b);
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if (a > 0 && b > 0 && Integer.MAX_VALUE / a < b ||
                a < 0 && b < 0 && Integer.MAX_VALUE / a > b ||
                a > 0 && b < 0 && Integer.MIN_VALUE / a > b ||
                a != -1 && a < 0 && b > 0 && Integer.MIN_VALUE / a < b) {
            throw new OverflowException(a + " * " + b);
        }
        return super.multiply(a, b);
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new OverflowException("-(2^31) / -1");
        }
        return super.divide(a, b);
    }

    @Override
    public Integer negate(Integer a) {
        if (a == Integer.MIN_VALUE) {
            throw new OverflowException("abs -(2^31)");
        }
        return super.negate(a);
    }

    @Override
    public Integer abs(Integer a) {
        if (a == Integer.MIN_VALUE) {
            throw new OverflowException("abs -(2^31)");
        }
        return super.abs(a);
    }
}
