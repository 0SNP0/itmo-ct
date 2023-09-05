package shell.units;

import java.util.List;

public class And extends Conditional {

    public And(List<Unit> list) {
        super(list);
    }

    public And(Unit... units) {
        super(units);
    }

    @Override 
    protected boolean doNext(boolean res) {
        return res;
    }
}
