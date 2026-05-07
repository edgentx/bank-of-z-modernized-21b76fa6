package com.vforce360.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

/**
 * Utility class to convert Markdown content to HTML.
 * FIXED: Resolved 'cannot find symbol' errors by adding Flexmark dependencies.
 */
public class MarkdownConverter {

    private static final MutableDataSet OPTIONS = new MutableDataSet();
    private static final Parser PARSER = Parser.builder(OPTIONS).build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

    public static String toHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        // Parse and render to HTML
        return RENDERER.render(PARSER.parse(markdown));
    }
}