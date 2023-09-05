package expression;

public class Main {
    public static void main(String[] args) {
//        System.out.println(new Add(
//                new Const(5),
//                new Subtract(
//                        new Const(2),
//                        new Const(3)
//                )).toMiniString());
//        System.out.println(new Subtract(
//                new Multiply(
//                        new Const(2),
//                        new Variable("x")
//                ),
//                new Const(3)
//        ).toMiniString());
//        Scanner in = new Scanner(System.in);
        final int x = 10;
        AbstractDoubleExpression expression = new Add(
                new PExpression() {
                    @Override
                    public int priority() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    public int evaluate(final int x) {
                        return 0;
                    }

                    @Override
                    public int evaluate(final int x, final int y, final int z) {
                        return 0;
                    }
                },
                new PExpression() {
                    @Override
                    public int priority() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    public int evaluate(final int x) {
                        return 0;
                    }

                    @Override
                    public int evaluate(final int x, final int y, final int z) {
                        return 0;
                    }
                }
        );
        System.out.println(expression.toMiniString());
        System.out.println(expression.evaluate(x));
//        final double d = in.nextDouble();
        System.out.println(expression.evaluate(0.0));
    }
}