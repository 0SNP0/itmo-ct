package expression;

public class Not extends UnaryOperation {

    public Not(PExpression operand) {
        super(operand);
    }

    @Override
    public int action(int x) {
        return ~x;
    }

    @Override
    public String operator() {
        return "~";
    }
}
