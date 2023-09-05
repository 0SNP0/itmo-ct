package expression.parser;

import java.util.Map;

public enum Type {

    HIGHEST(Integer.MAX_VALUE),
    LOWEST(0),
    BIN_LOW(10),
    BIN_HIGH(11),
    UN(20),
    VAR(HIGHEST),
    CONST(HIGHEST),
    ADD(BIN_LOW),
    SUB(BIN_LOW),
    MUL(BIN_HIGH),
    DIV(BIN_HIGH),
    MOD(BIN_HIGH),
    INV(UN),
    ABS(UN),
    SQ(UN),
    LP(HIGHEST),
    RP(LOWEST);

    public final int priority;

    Type(int priority) {
        this.priority = priority;
    }

    Type(Type base) {
        this(base.priority);
    }

/*
    public static final Map<Type, String> operators = Map.of(
            ADD, "+", SUB, "-", MUL, "*", DIV, "/", MOD, "mod",
            INV, "-", ABS, "abs", SQ, "square",
            LP, "(", RP, ")"
    );

    public String operator() {
        return operators.get(this);
    }
*/
}
