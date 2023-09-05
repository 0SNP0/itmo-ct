package markup;

public interface Mark extends MarkTeX {
    void toMarkdown(StringBuilder wrapper);
}
