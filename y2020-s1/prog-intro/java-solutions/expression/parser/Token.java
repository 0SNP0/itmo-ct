package expression.parser;

import expression.Type;

import static expression.Type.*;

public class Token {

    private final Type type;
    private String value;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(Type type) {
        this.type = type;
    }

    public String value() {
        return value;
    }

    public Type type() {
        return type;
    }

    public boolean isUnary() {
        return type.priority == UN.priority;
    }

    public boolean isVarOrConstOrLP() {
        return type.priority == Integer.MAX_VALUE;
    }

//    public boolean isBinary() {
//        return 0 < type.priority && type.priority < UN.priority;
//    }
}
