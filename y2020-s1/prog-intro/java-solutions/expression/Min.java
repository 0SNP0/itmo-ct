package expression;

public class Min extends AbstractExpression {

    public Min(PExpression operand1, PExpression operand2) {
        super(operand1, operand2);
    }

    @Override
    protected int action(int operand1, int operand2) {
        return operand1 < operand2 ? operand1 : operand2;
    }

    @Override
    protected String operator() {
        return "min";
    }

    @Override
    public int priority() {
        return Type.MIN.priority;
    }
}
