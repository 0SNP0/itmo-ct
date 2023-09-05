package markup;

import java.util.List;

public class UnorderedList extends AbstractList {

    public UnorderedList(List<ListItem> content) {
        super(content);
    }

    @Override
    public String texTag() {
        return "itemize";
    }
}
