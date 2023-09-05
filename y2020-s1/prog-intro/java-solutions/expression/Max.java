package expression;

public class Max extends AbstractExpression {

    public Max(PExpression operand1, PExpression operand2) {
        super(operand1, operand2);
    }

    @Override
    protected int action(int operand1, int operand2) {
        return operand1 > operand2 ? operand1 : operand2;
    }

    @Override
    protected String operator() {
        return "max";
    }

    @Override
    public int priority() {
        return Type.MAX.priority;
    }
}
