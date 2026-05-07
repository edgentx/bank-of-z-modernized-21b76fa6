package com.vforce360.mar.adapters;

import com.vforce360.mar.ports.MarkdownRendererPort;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Markdown rendering using Flexmark.
 */
@Component
public class FlexmarkMarkdownAdapter implements MarkdownRendererPort {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public FlexmarkMarkdownAdapter() {
        MutableDataSet options = new MutableDataSet();
        // Enable standard flexmark options if needed
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    @Override
    public String render(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}