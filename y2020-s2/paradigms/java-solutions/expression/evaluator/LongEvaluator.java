package expression.evaluator;

import expression.exceptions.DivisionByZeroException;

public class LongEvaluator implements Evaluator<Long> {

    @Override
    public Long add(Long a, Long b) {
        return a + b;
    }

    @Override
    public Long subtract(Long a, Long b) {
        return a - b;
    }

    @Override
    public Long multiply(Long a, Long b) {
        return a * b;
    }

    @Override
    public Long divide(Long a, Long b) {
        if (b == 0) {
            throw new DivisionByZeroException(b);
        }
        return a / b;
    }

    @Override
    public Long negate(Long a) {
        return -a;
    }

    @Override
    public Long abs(Long a) {
        return a < 0 ? -a : a;
    }

    @Override
    public Long mod(Long a, Long b) {
        if (b == 0) {
            throw new DivisionByZeroException(b);
        }
        return a % b;
    }

    @Override
    public Long parseNumber(String str) {
        return Long.parseLong(str);
    }

    @Override
    public Long castInt(int v) {
        return (long) v;
    }
}