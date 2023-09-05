package expression.generic;

import expression.GenericExpression;
import expression.evaluator.*;
import expression.exceptions.CalcException;
import expression.parser.ExpressionParser;

import java.util.Map;

public class GenericTabulator implements Tabulator {
    private final static Map<String, Evaluator<?>> EVALUATORS = Map.of(
            "i", new IntegerEvaluator(),
            "d", new DoubleEvaluator(),
            "bi", new BigIntegerEvaluator(),
            "u", new UncheckedIntegerEvaluator(),
            "l", new LongEvaluator(),
            "s", new ShortEvaluator()
    );

    @Override
    public Object[][][] tabulate(final String mode, final String expression, final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) throws Exception {
        return tabulate(EVALUATORS.get(mode), expression, x1, x2, y1, y2, z1, z2);
    }

    private <T> Object[][][] tabulate(final Evaluator<T> evaluator, final String expression, final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) throws Exception {
        final GenericExpression<T> expr = new ExpressionParser<T>().parse(expression);
        final Object[][][] table = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int i = 0; i <= x2 - x1; i++) {
            for (int j = 0; j <= y2 - y1; j++) {
                for (int k = 0; k <= z2 - z1; k++) {
                    try {
                        table[i][j][k] = expr.evaluate(evaluator.castInt(x1 + i),
                                evaluator.castInt(y1 + j), evaluator.castInt(z1 + k), evaluator);
                    } catch (final CalcException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }
        return table;
    }
}
