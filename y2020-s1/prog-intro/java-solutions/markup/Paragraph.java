package markup;

import java.util.List;

public class Paragraph implements InList {

    private List<InParagraph> content;

    public Paragraph(List<InParagraph> content) {
        this.content = content;
    }

    public void toMarkdown(StringBuilder wrapper) {
        for (Mark mark : content) {
            mark.toMarkdown(wrapper);
        }
    }

    @Override
    public void toTex(StringBuilder wrapper) {
        for (MarkTeX mark : content) {
            mark.toTex(wrapper);
        }
    }
}
