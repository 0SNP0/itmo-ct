package expression.evaluator;

import java.math.BigInteger;

import expression.exceptions.CalcException;
import expression.exceptions.DivisionByZeroException;

public class BigIntegerEvaluator implements Evaluator<BigInteger> {

    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger divide(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) == 0) {
            throw new DivisionByZeroException(b);
        }
        return a.divide(b);
    }

    @Override
    public BigInteger negate(BigInteger a) {
        return a.negate();
    }

    @Override
    public BigInteger abs(BigInteger a) {
        return a.abs();
    }

    @Override
    public BigInteger mod(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) == 0) {
            throw new DivisionByZeroException(b);
        }
        try {
            return a.mod(b);
        } catch (ArithmeticException e) {
            throw new CalcException(e.getMessage());
        }
    }

    @Override
    public BigInteger parseNumber(String str) {
        return new BigInteger(str);
    }

    @Override
    public BigInteger castInt(int v) {
        return BigInteger.valueOf(v);
    }
}
