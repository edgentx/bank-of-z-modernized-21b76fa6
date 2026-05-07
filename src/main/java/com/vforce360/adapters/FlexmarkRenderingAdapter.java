package com.vforce360.adapters;

import com.vforce360.ports.MarkdownRendererPort;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

/**
 * Real implementation of the Markdown Renderer using Flexmark.
 * This adapter handles the conversion of Markdown strings to HTML.
 */
public class FlexmarkRenderingAdapter implements MarkdownRendererPort {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public FlexmarkRenderingAdapter() {
        // Configure Flexmark options
        MutableDataSet options = new MutableDataSet();
        // Enable common extensions like tables and strikethrough
        // Note: Flexmark-all bundles most extensions.
        
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    @Override
    public String renderToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        // Parse and render
        return renderer.render(parser.parse(markdown));
    }
}
