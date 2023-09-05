package expression.exceptions;

public class Test {

    public static void main(String[] args) throws ParseException {
        System.out.println(new ExpressionParser().parse("x-x+y-y+z-(z)").toMiniString());
    }
}
