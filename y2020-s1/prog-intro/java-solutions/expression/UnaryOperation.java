package expression;

import java.util.Objects;

public abstract class UnaryOperation implements PExpression {

    private final PExpression operand;

    public UnaryOperation(PExpression operand) {
        this.operand = operand;
    }

    public abstract int action(int x);
    public abstract String operator();

    @Override
    public int evaluate(int x) {
        return action(operand.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return action(operand.evaluate(x, y, z));
    }

    @Override
    public int priority() {
        return Type.UN.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand, this.getClass());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        UnaryOperation expression = (UnaryOperation) obj;
        return this.operand.equals(expression.operand);
    }

    @Override
    public String toMiniString() {
        String o = operand.toMiniString();
        if (operand instanceof AbstractExpression) {
            o = "(" + o + ")";
        }
        return operator() + o;
    }

    @Override
    public String toString() {
        return "(" + operator() + operand.toString() + ")";
    }
}
