package expression;

public class Negate extends UnaryOperation {

    public Negate(PExpression operand) {
        super(operand);
    }

    @Override
    public int action(int x) {
        return -x;
    }

    @Override
    public String operator() {
        return "-";
    }
}
