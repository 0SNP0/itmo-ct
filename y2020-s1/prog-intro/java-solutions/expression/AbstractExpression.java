package expression;

import java.util.Objects;

public abstract class AbstractExpression implements PExpression {

    protected final PExpression operand1, operand2;

    public AbstractExpression(PExpression operand1, PExpression operand2) {
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    protected abstract int action(int operand1, int operand2);
    protected abstract String operator();

    @Override
    public int evaluate(int x) {
        return action(operand1.evaluate(x), operand2.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return action(operand1.evaluate(x, y, z), operand2.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        AbstractExpression expression = (AbstractExpression) obj;
        return this.operand1.equals(expression.operand1) && this.operand2.equals(expression.operand2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand1, operand2, this.getClass());
    }

    @Override
    public String toString() {
        return "(" + operand1 + " " + operator() + " " + operand2 + ")";
    }

    @Override
    public String toMiniString() {
        // :NOTE: копипаста
        String o1 = operand1.toMiniString();
        if (operand1.priority() < this.priority()) {
            o1 = "(" + o1 + ")";
        }

        String o2 = operand2.toMiniString();
        if (operand2.priority() < this.priority() || operand2.priority() == this.priority() && (this.brackets() || operand2.brackets()) && this.getClass() != Add.class) {
            o2 = "(" + o2 + ")";
        }
        return o1 + " " + operator() + " " + o2;
    }

}
