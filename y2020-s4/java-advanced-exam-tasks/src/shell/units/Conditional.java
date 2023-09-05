package shell.units;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

public abstract class Conditional extends MultiUnit<Unit> {

    protected Conditional(List<Unit> list) {
        super(list);
    }

    protected Conditional(Unit... units) {
        super(units);
    }

    public Conditional add(Unit unit) {
        list.add(unit);
        return this;
    }

    @Override
    public Redirect in() {
        return list.get(0).in();
    }

    @Override
    public Redirect out() {
        return list.get(0).out();
    }

    @Override
    public Unit setIn(Redirect in) {
        list.forEach(x -> x.setIn(in));
        return this;
    }

    @Override
    public Unit setOut(Redirect out) {
        list.forEach(x -> x.setOut(out));
        return this;
    }

    @Override
    public boolean eval() {
        boolean res = false;
        for (Unit unit : list) {
            res = unit.eval();
            if (!doNext(res))
                break;
        }
        return res;
    }

    @Override
    public Process start() throws IOException {
        throw new UnsupportedOperationException();
    }

    protected abstract boolean doNext(boolean res);
}
