package com.vforce360.mar.adapters;

import com.vforce360.mar.ports.MarkdownRendererPort;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.stereotype.Component;

/**
 * Adapter implementation for rendering Markdown using Flexmark.
 * This concrete class is separated from the core logic and can be swapped.
 */
@Component
public class FlexmarkMarkdownAdapter implements MarkdownRendererPort {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public FlexmarkMarkdownAdapter() {
        MutableDataSet options = new MutableDataSet();
        // Configure Flexmark options here if needed
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    @Override
    public String renderToHtml(String markdown) {
        if (markdown == null) {
            return "";
        }
        Document document = parser.parse(markdown);
        return renderer.render(document);
    }
}