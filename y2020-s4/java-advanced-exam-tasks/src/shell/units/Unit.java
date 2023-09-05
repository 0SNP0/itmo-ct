package shell.units;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;

public interface Unit {
    default boolean eval() {
        try {
            return start().waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getLocalizedMessage());
        }
        return false;
    }
    Redirect in();
    Unit setIn(Redirect in);
    Redirect out();
    Unit setOut(Redirect out);
    default Unit setOut(OutputStream out) {
        throw new UnsupportedOperationException();
    }
    default Unit init(Redirect in, Redirect out) {
        setIn(in);
        setOut(out);
        return this;
    }
    default Unit stdio() {
        return init(Redirect.INHERIT, Redirect.INHERIT);
    }
    Process start() throws IOException;
}
