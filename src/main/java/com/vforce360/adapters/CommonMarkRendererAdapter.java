package com.vforce360.adapters;

import com.vforce360.ports.ReportRendererPort;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

/**
 * Real adapter for rendering Markdown using the CommonMark library.
 * This connects to the external library (CommonMark) via the ReportRendererPort interface.
 */
@Component
public class CommonMarkRendererAdapter implements ReportRendererPort {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public CommonMarkRendererAdapter() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    @Override
    public String renderMarkdownToHtml(String markdown) {
        if (markdown == null) {
            return "";
        }
        // Parse markdown to a document node
        Node document = parser.parse(markdown);
        // Render the node to HTML
        return renderer.render(document);
    }
}