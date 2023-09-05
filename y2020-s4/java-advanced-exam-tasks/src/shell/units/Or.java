package shell.units;

import java.util.List;

public class Or extends Conditional {

    public Or(List<Unit> list) {
        super(list);
    }

    public Or(Unit... units) {
        super(units);
    }

    @Override
    protected boolean doNext(boolean res) {
        return !res;
    }
    
}
