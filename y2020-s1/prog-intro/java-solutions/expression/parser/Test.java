package expression.parser;

public class Test {

    public static void main(String[] args) {
        System.out.println(new ExpressionParser().parse("x -1").toString());
        var t = new ExpressionParser().toTokens("x -1");
        for (var e : t) {
            System.out.println(e.type());
        }
    }
}
