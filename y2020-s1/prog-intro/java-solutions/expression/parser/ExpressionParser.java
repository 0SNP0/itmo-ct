package expression.parser;

import expression.*;

import java.util.*;
import static expression.Type.*;

public class ExpressionParser implements Parser {

    @Override
    public TripleExpression parse(String expression) {
        final List<Token> tokens = toTokens(expression);
        return getExpression(tokens.iterator());
    }

    public List<Token> toTokens(String str) {
        final LinkedList<Token> list = new LinkedList<>();
        final TokenSource source = new TokenSource(str);
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
                    type = LP;
                    break;
                case ")":
                    type = RP;
                    break;
                case "count":
                    type = CNT;
                    break;
                default:
                    if (Character.isLetter(token.charAt(0))) {
                        type = VAR;
                    } else {
                        throw new IllegalStateException("Unexpected value: " + token);
                    }
            }
            list.addLast((type == CONST || type == VAR) ? new Token(type, token) : new Token(type));
        }
        list.addLast(new Token(RP));
        return list;
    }

    public PExpression getExpression(Iterator<Token> iter) {
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
                    expr = new Const(Integer.parseInt(token.value()));
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
            case ADD: return new Add(o1, o2);
            case SUB: return new Subtract(o1, o2);
            case MUL: return new Multiply(o1, o2);
            case DIV: return new Divide(o1, o2);
            case AND: return new And(o1, o2);
            case XOR: return new Xor(o1, o2);
            case OR: return new Or(o1, o2);
            default: throw new IllegalStateException("Unexpected binary operation: " + type);
        }
    }

    private PExpression unaryAction (PExpression o, Type type) {
        switch (type) {
            case INV: return new Negate(o);
            case NOT: return new Not(o);
            case CNT: return new Count(o);
            default: throw new IllegalStateException("Unexpected unary operation: " + type);
        }
    }

    private boolean nextAsUnary(Type type) {
        return type != RP && type != CONST && type != VAR;
    }
}
