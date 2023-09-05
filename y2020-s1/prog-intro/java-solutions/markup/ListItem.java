package markup;

import java.util.List;

public class ListItem implements MarkTeX {

    public List<InList> content;

    public ListItem(List<InList> content) {
        this.content = content;
    }

    private String texTag() {
        return "\\item ";
    }

    @Override
    public void toTex(StringBuilder wrapper) {
        wrapper.append(texTag());
        for (MarkTeX mark : content) {
            mark.toTex(wrapper);
        }
    }
}
