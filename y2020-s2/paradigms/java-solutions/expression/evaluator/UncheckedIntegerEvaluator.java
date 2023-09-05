package expression.evaluator;

import expression.exceptions.DivisionByZeroException;

public class UncheckedIntegerEvaluator implements Evaluator<Integer> {

    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) {
            throw new DivisionByZeroException(b);
        }
        return a / b;
    }

    @Override
    public Integer negate(Integer a) {
        return -a;
    }

    @Override
    public Integer abs(Integer a) {
        return a < 0 ? -a : a;
    }

    @Override
    public Integer mod(Integer a, Integer b) {
        if (b == 0) {
            throw new DivisionByZeroException(b);
        }
        return a % b;
    }

    @Override
    public Integer parseNumber(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public Integer castInt(int v) {
        return v;
    }
}
