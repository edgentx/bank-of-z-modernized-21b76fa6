package com.vforce360.adapters;

import com.vforce360.ports.MarkdownRendererPort;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

/**
 * Implementation of MarkdownRendererPort using the Flexmark library.
 * This adapter converts Markdown text to HTML using a standard configuration.
 */
public class FlexmarkRenderingAdapter implements MarkdownRendererPort {

    private final Parser parser;
    private final HtmlRenderer renderer;

    /**
     * Default constructor. Initializes the Flexmark parser and renderer with standard options.
     * Using MutableDataSet to configure options (though defaults are sufficient for this fix).
     */
    public FlexmarkRenderingAdapter() {
        MutableDataSet options = new MutableDataSet();
        // No specific options needed for basic rendering, but object required for builder
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    /**
     * Converts the provided markdown string to HTML.
     * 
     * @param markdown The markdown string to render.
     * @return Rendered HTML string.
     */
    @Override
    public String renderToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        // Parse and render
        org.commonmark.node.Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
