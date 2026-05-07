package com.vforce360.adapters;

import com.vforce360.shared.ports.MarkdownRendererPort;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

/**
 * Concrete adapter for Markdown rendering using CommonMark library.
 * This implements the port defined in the shared layer.
 */
@Component
public class CommonMarkRendererAdapter implements MarkdownRendererPort {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public CommonMarkRendererAdapter() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    @Override
    public String renderToHtml(String markdownContent) {
        if (markdownContent == null || markdownContent.isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdownContent);
        return renderer.render(document);
    }
}
