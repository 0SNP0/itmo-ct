package expression;

public class Subtract extends AbstractDoubleExpression {

    public Subtract(PExpression operand1, PExpression operand2) {
        super(operand1, operand2);
    }

    @Override
    protected int action(int operand1, int operand2) {
        return operand1 - operand2;
    }

    @Override
    protected double action(double operand1, double operand2) {
        return operand1 - operand2;
    }

    @Override
    protected String operator() {
        return "-";
    }

    @Override
    public int priority() {
        return Type.SUB.priority;
    }

    @Override
    public boolean brackets() {
        return true;
    }
}
