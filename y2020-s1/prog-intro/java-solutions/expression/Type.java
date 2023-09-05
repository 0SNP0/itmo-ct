package expression;

public enum Type {

    VAR(Integer.MAX_VALUE),
    CONST(Integer.MAX_VALUE),
    OR(6),
    XOR(7),
    AND(8),
    ADD(10),
    SUB(10),
    MUL(11),
    DIV(11),
    INV(20),
    NOT(20),
    CNT(20),
    LP(Integer.MAX_VALUE),
    RP(0),
    UN(20),
    ABS(20),
    SQRT(20),
    MIN(1),
    MAX(1);

    public final int priority;

    Type(int priority) {
        this.priority = priority;
    }

}
