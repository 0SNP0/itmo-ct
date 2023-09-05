package expression.evaluator;

public interface Evaluator<T> {
    T add(T a, T b);
    T subtract(T a, T b);
    T multiply(T a, T b);
    T divide(T a, T b);
    T negate(T a);
    T abs(T a);
    default T square(T a) {
        return multiply(a, a);
    }
    T mod(T a, T b);
    T parseNumber(String str);
    T castInt(int v);
}
