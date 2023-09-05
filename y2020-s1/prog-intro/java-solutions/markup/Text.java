package markup;

public class Text implements InParagraph {

    private String content;

    public Text(String text) {
        content = text;
    }

    @Override
    public void toTex(StringBuilder wrapper) {
        wrapper.append(content);
    }

    @Override
    public void toMarkdown(StringBuilder wrapper) {
        wrapper.append(content);
    }
}
