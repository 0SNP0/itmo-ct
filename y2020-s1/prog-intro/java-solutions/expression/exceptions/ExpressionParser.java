package expression.exceptions;

import expression.*;
import expression.parser.Token;
import expression.parser.TokenSource;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static expression.Type.*;

public class ExpressionParser implements Parser {

    @Override
    public TripleExpression parse(String expression) throws ParseException {
        final List<Token> tokens = toTokens(expression);
        return getExpression(tokens.iterator());
    }

    public List<Token> toTokens(String str) throws ParseException {
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
                case "~":
                    type = NOT;
                    break;
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
                case "&":
                    type = AND;
                    break;
                case "^":
                    type = XOR;
                    break;
                case "|":
                    type = OR;
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
                case "count":
                    type = CNT;
                    break;
                case "abs":
                    type = ABS;
                    break;
                case "sqrt":
                    type = SQRT;
                    break;
                case "min":
                    type = MIN;
                    break;
                case "max":
                    type = MAX;
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

    public PExpression getExpression(Iterator<Token> iter) throws ConstantOverflowException {
        final Stack<Type> binary = new Stack<>();
        final Stack<Type> unary = new Stack<>();
        final Stack<PExpression> expressions = new Stack<>();
        while (iter.hasNext()) {
            final Token token = iter.next();
            if (token.isUnary()) {
                unary.add(token.type());
            } else if (token.isVarOrConstOrLP()) {
                PExpression expr;
                if (token.type() == VAR) {
                    expr = new Variable(token.value());
                } else if (token.type() == CONST) {
                    try {
                        expr = new Const(Integer.parseInt(token.value()));
                    } catch (NumberFormatException e) {
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
                    final PExpression o2 = expressions.pop();
                    final PExpression o1 = expressions.pop();
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

    private PExpression binaryAction (PExpression o1, PExpression o2, Type type) {
        switch (type) {
            case ADD: return new CheckedAdd(o1, o2);
            case SUB: return new CheckedSubtract(o1, o2);
            case MUL: return new CheckedMultiply(o1, o2);
            case DIV: return new CheckedDivide(o1, o2);
            case AND: return new And(o1, o2);
            case XOR: return new Xor(o1, o2);
            case OR: return new Or(o1, o2);
            case MIN: return new Min(o1, o2);
            case MAX: return new Max(o1, o2);
            default: throw new IllegalStateException("Unexpected binary operation: " + type);
        }
    }

    private PExpression unaryAction (PExpression o, Type type) {
        switch (type) {
            case INV: return new CheckedNegate(o);
            case NOT: return new Not(o);
            case CNT: return new Count(o);
            case ABS: return new Abs(o);
            case SQRT: return new Sqrt(o);
            default: throw new IllegalStateException("Unexpected unary operation: " + type);
        }
    }

    private boolean nextAsUnary(Type type) {
        return type != RP && type != CONST && type != VAR;
    }
}
