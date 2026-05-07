package com.vforce360.mar.ports;

/**
 * Port for rendering Markdown text into HTML.
 * This allows the core logic to remain decoupled from specific Markdown parsing libraries.
 */
public interface MarkdownRendererPort {

    /**
     * Converts Markdown formatted text to HTML.
     * @param markdown The raw Markdown string.
     * @return The rendered HTML string.
     */
    String renderToHtml(String markdown);
}