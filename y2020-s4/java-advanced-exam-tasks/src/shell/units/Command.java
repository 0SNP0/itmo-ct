package shell.units;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;

import common.Utils;

public class Command implements Unit {
    private final ProcessBuilder pb;
    // private OutputStream os;

    public Command(String[] command) {
        pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
    }

    public Command(String command) {
        this(Utils.split(command));
    }

    public ProcessBuilder processBuilder() {
        return pb;
    }

    @Override
    public Redirect in() {
        return pb.redirectInput();
    }

    @Override
    public Redirect out() {
        return pb.redirectOutput();
    }
    
    @Override
    public Process start() throws IOException {
        Process process = pb.start();
        // if (os != null) {
        //     new Redirector(process.getInputStream(), os).run();
        // }
        return process;
    }

    @Override
    public Unit setIn(Redirect in) {
        pb.redirectInput(in);
        return this;
    }

    @Override
    public Unit setOut(Redirect out) {
        pb.redirectOutput(out);
        return this;
    }

    // @Override
    // public Unit setOut(OutputStream out) {
    //     os = out;
    //     return this;
    // }

    public static Command c(String... command) {
        if (command.length == 1) {
            return new Command(command[0]);
        }
        return new Command(command);
    }
}
