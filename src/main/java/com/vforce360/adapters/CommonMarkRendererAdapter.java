package com.vforce360.adapters;

import com.vforce360.ports.MarkdownRendererPort;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Adapter for the Atlassian CommonMark library.
 * Handles the conversion of Markdown strings to HTML.
 */
public class CommonMarkRendererAdapter implements MarkdownRendererPort {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public CommonMarkRendererAdapter() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    @Override
    public String renderToHtml(String markdown) {
        if (markdown == null) {
            return "";
        }
        // CommonMark requires parsing the document node first
        return renderer.render(parser.parse(markdown));
    }
}
