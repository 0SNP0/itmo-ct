package expression;

public class Const implements PExpression, DoubleExpression {

    private final Number value;

    public Const(int x) {
        this.value = x;
    }

    public Const(double x) {
        this.value = x;
    }

    @Override
    public int priority() {
        return Type.CONST.priority;
    }

    @Override
    public boolean brackets() {
        return false;
    }

    @Override
    public double evaluate(double x) {
        return this.value.doubleValue();
    }

    @Override
    public int evaluate(int x) {
        if (this.value.getClass() == Double.class) {
            throw new AssertionError("Const is double");
        }
        return (int) this.value;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return evaluate(x);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != Const.class) {
            return false;
        }
        Const c = (Const) obj;
        return this.value.equals(c.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
