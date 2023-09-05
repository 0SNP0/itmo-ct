package shell.units;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

public class Pipe extends MultiUnit<Command> {

    public Pipe(List<Command> list) {
        super(list);
    }

    public Pipe(Command... units) {
        super(units);
    }
    
    @Override
    public Pipe add(Command command) {
        super.add(command);
        return this;
    }

    @Override
    public Redirect in() {
        return list.get(0).in();
    }

    @Override
    public Unit setIn(Redirect in) {
        list.get(0).setIn(in);
        return this;
    }

    @Override
    public Redirect out() {
        return list.get(list.size() - 1).out();
    }

    @Override
    public Unit setOut(Redirect out) {
        list.get(list.size() - 1).setOut(out);
        return this;
    }

    @Override
    public Process start() throws IOException {
        List<Process> proc = ProcessBuilder.startPipeline(list.stream().map(Command::processBuilder).toList());
        return proc.get(proc.size() - 1);
    }
}
