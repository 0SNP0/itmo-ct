package expression;

public class Or extends AbstractExpression {

    public Or(PExpression operand1, PExpression operand2) {
        super(operand1, operand2);
    }

    @Override
    protected int action(int operand1, int operand2) {
        return operand1 | operand2;
    }

    @Override
    protected String operator() {
        return "|";
    }

    @Override
    public int priority() {
        return Type.OR.priority;
    }
}
