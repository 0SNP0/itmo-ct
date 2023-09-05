package expression;

public interface PExpression extends Expression, TripleExpression {
    int priority();
    default boolean brackets() {
        return false;
    }
}
