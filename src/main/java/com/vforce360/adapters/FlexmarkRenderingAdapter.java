package com.vforce360.adapters;

import com.vforce360.ports.RenderingEnginePort;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Adapter implementation using Flexmark to render Markdown to HTML.
 * This implements the RenderingEnginePort interface.
 */
@Component
public class FlexmarkRenderingAdapter implements RenderingEnginePort {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public FlexmarkRenderingAdapter() {
        // Configure Flexmark options (Tables, Strikethrough support for GFM)
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));
        
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    @Override
    public String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        // Use the Flexmark parser/renderer to convert MD to HTML
        return renderer.render(parser.parse(markdown));
    }
}
