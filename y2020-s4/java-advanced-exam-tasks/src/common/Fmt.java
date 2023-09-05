package common;

public class Fmt {
    public static String wrap(String str, String fmt) {
        return String.format("%s%s%s", fmt, str, RESET);
    }

    public static void println(String str, String fmt) {
        System.out.printf("%s%s%s%n", fmt, str, RESET);
    }

    public static final String RESET = "\u001B[0m", BOLD = "\u001B[1m",
            BLACK = "\u001B[30m", RED = "\u001B[31m", GREEN = "\u001B[32m", YELLOW = "\u001B[33m",
            BLUE = "\u001B[34m", PURPLE = "\u001B[35m", CYAN = "\u001B[36m", WHITE = "\u001B[37m";
}
