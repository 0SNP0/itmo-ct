package shell;

import common.Fmt;
import common.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Shell {
    private static final String HELP = "help", EXIT = "exit";
    private static final String username = Utils.username(), hostname = Utils.hostname();

    private static void run(String line) {
        try {
            Parser.parse(line).stdio().eval();
        } catch (Parser.ParserException e) {
            System.err.println(e.getLocalizedMessage());
            Fmt.println("^Error^", Fmt.RED);
        }
    }

    public static void main(String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            while (true) {
                printGreet();
                String line = reader.readLine();
                if (line == null || line.equals(EXIT)) {
                    break;
                }
                if (line.equals("")) {
                    continue;
                }
                if (line.equals(HELP)) {
                    help();
                } else {
                    run(line);
                }
            }
            Fmt.println("Exiting...", Fmt.PURPLE);
        } catch (IOException e) {
            System.err.println("Input error");
        }
    }

    private static void printGreet() {
        System.out.printf("%s: %s> ", 
        Fmt.wrap(username + "@" + hostname, Fmt.GREEN),
        Fmt.wrap(Paths.get(".").toAbsolutePath().normalize().toString(), Fmt.CYAN));
    }

    private static void help() {
        Fmt.println("help message", Fmt.YELLOW);
    }
}
