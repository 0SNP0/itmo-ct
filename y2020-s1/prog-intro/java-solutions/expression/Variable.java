package expression;

public class Variable implements PExpression, DoubleExpression {

    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public int priority() {
        return Type.VAR.priority;
    }

    @Override
    public boolean brackets() {
        return false;
    }

    @Override
    public double evaluate(double x) {
        return x;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        switch (name) {
            case "x": return x;
            case "y": return y;
            case "z": return z;
            default: throw new IllegalStateException("Unexpected variable name: " + name);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != Variable.class) {
            return false;
        }
        return name.equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
