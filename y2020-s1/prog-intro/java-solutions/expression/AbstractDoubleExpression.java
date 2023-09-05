package expression;

public abstract class AbstractDoubleExpression extends AbstractExpression implements DoubleExpression {

    public AbstractDoubleExpression(PExpression operand1, PExpression operand2) {
        super(operand1, operand2);
    }

    protected abstract double action(double operand1, double operand2);

    // :NOTE: Неверные типы
    @Override
    public double evaluate(double x) {
        return action(((DoubleExpression) operand1).evaluate(x), ((DoubleExpression) operand2).evaluate(x));
    }
}
