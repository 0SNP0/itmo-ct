package markup;

import java.util.List;

public class Strikeout extends AbstractMark {

    public Strikeout(List<Mark> content) {
        super(content);
    }

    @Override
    protected String mdTag() {
        return "~";
    }

    @Override
    protected String texTag() {
        return "textst";
    }
}
