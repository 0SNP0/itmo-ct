package markup;

import java.util.List;

public class Strong extends AbstractMark {

    public Strong(List<Mark> content) {
        super(content);
    }

    @Override
    protected String mdTag() {
        return "__";
    }

    @Override
    protected String texTag() {
        return "textbf";
    }

//    static {
////        new Strong(List.of(new Paragraph(List.of())));
//    }
}
