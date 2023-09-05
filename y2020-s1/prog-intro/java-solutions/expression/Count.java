package expression;

public class Count extends UnaryOperation {

    public Count(PExpression operand) {
        super(operand);
    }

    @Override
    public int action(int x) {
        int count = 0;
        for (; x != 0; x = x >>> 1) {
            count += (x & 1);
        }
        return count;
    }

    @Override
    public String operator() {
        return "count ";
    }
}
