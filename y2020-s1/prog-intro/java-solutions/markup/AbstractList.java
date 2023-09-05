package markup;

import java.util.List;

public abstract class AbstractList implements InList {

    protected List<ListItem> content;

    protected AbstractList(List<ListItem> content) {
        this.content = content;
    }

    public abstract String texTag();

    @Override
    public void toTex(StringBuilder wrapper) {
        wrapper.append("\\begin{").append(texTag()).append("}");
        for (ListItem mark : content) {
            mark.toTex(wrapper);
        }
        wrapper.append("\\end{").append(texTag()).append("}");
    }
}
