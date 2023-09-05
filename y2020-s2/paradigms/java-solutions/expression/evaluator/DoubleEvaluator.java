package expression.evaluator;

public class DoubleEvaluator implements Evaluator<Double> {

    @Override
    public Double add(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double subtract(Double a, Double b) {
        return a - b;
    }

    @Override
    public Double multiply(Double a, Double b) {
        return a * b;
    }

    @Override
    public Double divide(Double a, Double b) {
        return a / b;
    }

    @Override
    public Double negate(Double a) {
        return -a;
    }

    @Override
    public Double abs(Double a) {
        return a < 0 ? -a : a;
    }

    @Override
    public Double mod(Double a, Double b) {
        return a % b;
    }

    @Override
    public Double parseNumber(String str) {
        return Double.parseDouble(str);
    }

    @Override
    public Double castInt(int v) {
        return (double) v;
    }
}
