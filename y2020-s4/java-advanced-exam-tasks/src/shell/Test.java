package shell;

import java.io.IOException;

import shell.units.*;
import static shell.units.Command.c;

public class Test {
    public static void main(String... args) throws IOException, InterruptedException {
        System.out.println(new Command("sort").stdio().eval());
        // System.out.println(
        //     new OldPipe(new Command("sort"), new Command("tac")).stdio().eval()
        // );
        // System.out.println(new OldPipe(
        //     new OldPipe(new Command("sort"), new Command("tac")),
        //     new Command("head -2")).stdio().eval()
        // );
        System.out.println(
            new Or(
                new And(
                    new Pipe(c("sort"), c("tac")).add(c("head -2")),
                    c("asdfgfsd")
                )).add(
                    new Pipe(c("ls"), c("cat"))
                )
            .stdio().eval()
        );
    }
}
