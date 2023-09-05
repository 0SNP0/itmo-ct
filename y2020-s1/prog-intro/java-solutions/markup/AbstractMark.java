package markup;

import java.util.List;

public abstract class AbstractMark implements InParagraph {

    protected List<Mark> content;

    protected AbstractMark(List<Mark> content) {
        this.content = content;
    }

    protected abstract String mdTag();

    protected abstract String texTag();

    @Override
    public void toMarkdown(StringBuilder wrapper) {
        wrapper.append(mdTag());
        for (Mark mark : content) {
            mark.toMarkdown(wrapper);
        }
        wrapper.append(mdTag());
    }
    @Override
    public void toTex(StringBuilder wrapper) {
        wrapper.append("\\").append(texTag()).append("{");
        for (MarkTeX mark : content) {
            mark.toTex(wrapper);
        }
        wrapper.append("}");
    }
}
