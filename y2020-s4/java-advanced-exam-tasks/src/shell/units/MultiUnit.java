package shell.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MultiUnit<U extends Unit> implements Unit {
    
    protected List<U> list;

    protected MultiUnit(List<U> list) {
        this.list = new ArrayList<>(list);
    }

    protected MultiUnit(U... units) {
        this(Arrays.asList(units));
    }

    public MultiUnit<U> add(U unit) {
        list.add(unit);
        return this;
    }
}
