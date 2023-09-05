package markup;

import java.util.List;

public class Emphasis extends AbstractMark {

    public Emphasis(List<Mark> content) {
        super(content);
    }

    @Override
    protected String texTag() {
        return "emph";
    }

    @Override
    protected String mdTag() {
        return "*";
    }
}
