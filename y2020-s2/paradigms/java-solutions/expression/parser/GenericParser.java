package expression.parser;

import expression.GenericExpression;
import expression.exceptions.ParseException;

public interface GenericParser<T> {
    GenericExpression<T> parse(String expression) throws ParseException;
}
