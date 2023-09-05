package expression.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import expression.GenericExpression;
import expression.exceptions.*;
import expression.*;
import static expression.parser.Type.*;

public class ExpressionParser<T> implements GenericParser<T> {
    @Override
    public GenericExpression<T> parse(final String expression) throws ParseException {
        final List<Token> tokens = toTokens(expression);
        return getExpression(tokens.iterator());
    }

    public List<Token> toTokens(final String str) throws ParseException {
        final LinkedList<Token> list = new LinkedList<>();
        final TokenSource source = new TokenSource(str);
        int depth = 0;
        while (source.hasNext()) {
            String token = source.next();
            final Type type;
            if (Character.isDigit(token.charAt(0))) {
                if (!list.isEmpty() && list.getLast().type() == INV && source.prevBack() == '-') {
                    list.removeLast();
                    token = "-" + token;
                }
                type = CONST;
            } else switch (token) {
                case "-":
                    type = (list.isEmpty() || nextAsUnary(list.getLast().type())) ? INV : SUB;
                    break;
                case "+":
                    type = ADD;
                    break;
                case "*":
                    type = MUL;
                    break;
                case "/":
                    type = DIV;
                    break;
                case "mod":
                    type = MOD;
                    break;
                case "(":
                    depth++;
                    type = LP;
                    break;
                case ")":
                    if (--depth < 0) {
                        throw new MissingLexemeException("No opening parenthesis: " + str);
                    }
                    if (list.getLast().type() != CONST && list.getLast().type() != VAR && list.getLast().type() != RP) {
                        throw new MissingLexemeException("No last argument': " + list.getLast().type() + " RP");
                    }
                    type = RP;
                    break;
                case "abs":
                    type = ABS;
                    break;
                case "square":
                    type = SQ;
                    break;
                default:
                    if (token.equals("x") || token.equals("y") || token.equals("z")) {
                        type = VAR;
                    } else {
                        throw new UnknownLexemeException(token);
                    }
            }
            if (!list.isEmpty()) {
                if (type == list.getLast().type()) {
                    if (type == CONST) {
                        throw new MissingLexemeException("Spaces in numbers: " + list.getLast().value() + " " + token);
                    } else  if (type == VAR) {
                        throw new MissingLexemeException("Two variables in a row: " + list.getLast().value() + " " + token);
                    }
                } else if (type.priority == list.getLast().type().priority
                        && list.getLast().isVarOrConstOrLP() && list.getLast().type() != LP) {
                    throw new MissingLexemeException(list.getLast().type() + " [here] " + type);
                }
                if (type.priority < UN.priority && type.priority > 0
                        && list.getLast().type().priority < Integer.MAX_VALUE && list.getLast().type().priority > 0) {
                    throw new MissingLexemeException("No middle argument: " + list.getLast().type() + " [here] " + type);
                }
            }
            if ((list.isEmpty() || list.getLast().type() == LP) && type.priority < UN.priority && type.priority > 0) {
                throw new MissingLexemeException("No first argument before " + token);
            }
            list.addLast((type == CONST || type == VAR) ? new Token(type, token) : new Token(type));
        }
        if (depth > 0) {
            throw new MissingLexemeException("No closing parenthesis: " + str);
        }
        if (list.isEmpty()) {
            throw new MissingLexemeException("Empty expression: " + str);
        }
        if (list.getLast().type() != CONST && list.getLast().type() != VAR && list.getLast().type() != RP) {
            throw new MissingLexemeException("No last argument: " + str);
        }
        list.addLast(new Token(RP));
        return list;
    }

    public GenericExpression<T> getExpression(final Iterator<Token> iter) throws ConstantOverflowException {
        final Stack<Type> binary = new Stack<>();
        final Stack<Type> unary = new Stack<>();
        final Stack<GenericExpression<T>> expressions = new Stack<>();
        while (iter.hasNext()) {
            final Token token = iter.next();
            if (token.isUnary()) {
                unary.add(token.type());
            } else if (token.isVarOrConstOrLP()) {
                GenericExpression<T> expr;
                if (token.type() == VAR) {
                    expr = new Variable<T>(token.value());
                } else if (token.type() == CONST) {
                    try {
                        expr = new Const<T>(token.value());
                    } catch (final NumberFormatException e) {
                        throw new ConstantOverflowException(token.value());
                    }
                } else {
                    expr = getExpression(iter);
                }
                while (!unary.isEmpty()) {
                    expr = unaryAction(expr, unary.pop());
                }
                expressions.add(expr);
            } else {
                while (!binary.isEmpty() && token.type().priority <= binary.peek().priority) {
                    final GenericExpression<T> o2 = expressions.pop();
                    final GenericExpression<T> o1 = expressions.pop();
                    expressions.add(binaryAction(o1, o2, binary.pop()));
                }
                if (token.type() == RP) {
                    break;
                } else {
                    binary.add(token.type());
                }
            }
        }
        return expressions.pop();
    }

    private GenericExpression<T> binaryAction (final GenericExpression<T> o1, final GenericExpression<T> o2, final Type type) {
        switch (type) {
            case ADD: return new Add<T>(o1, o2);
            case SUB: return new Subtract<T>(o1, o2);
            case MUL: return new Multiply<T>(o1, o2);
            case DIV: return new Divide<T>(o1, o2);
            case MOD: return new Mod<T>(o1, o2);
            default: throw new IllegalStateException("Unexpected binary operation: " + type);
        }
    }

    private GenericExpression<T> unaryAction (final GenericExpression<T> o, final Type type) {
        switch (type) {
            case INV: return new Negate<T>(o);
            case SQ: return new Square<T>(o);
            case ABS: return new Abs<T>(o);
            default: throw new IllegalStateException("Unexpected unary operation: " + type);
        }
    }

    private boolean nextAsUnary(final Type type) {
        return type != RP && type != CONST && type != VAR;
    }
}
