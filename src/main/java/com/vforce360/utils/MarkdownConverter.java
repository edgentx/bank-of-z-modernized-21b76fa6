package com.vforce360.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Document;

/**
 * Utility wrapper for Flexmark library.
 * Handles the conversion of Markdown text to HTML.
 */
public class MarkdownConverter {

    // Reusable parser and renderer instances for performance
    private static final Parser PARSER;
    private static final HtmlRenderer RENDERER;

    static {
        // Configure Flexmark with standard options (Tables, Autolinks, etc.)
        PARSER = Parser.builder(PegdownOptionsAdapter.flexmarkOptions(true, Extensions.TABLES, Extensions.AUTOLINKS, Extensions.FENCED_CODE_BLOCKS)).build();
        RENDERER = HtmlRenderer.builder().build();
    }

    /**
     * Converts a Markdown string to HTML.
     * 
     * @param markdown The markdown content.
     * @return HTML string.
     */
    public static String toHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        Document document = PARSER.parse(markdown);
        return RENDERER.render(document);
    }
}
