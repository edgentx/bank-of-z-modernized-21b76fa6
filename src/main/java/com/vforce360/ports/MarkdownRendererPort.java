package com.vforce360.ports;

/**
 * Port interface for converting Markdown content to HTML.
 * This allows switching between different markdown libraries (CommonMark, Flexmark, Pegdown) 
 * without changing the application logic.
 */
public interface MarkdownRendererPort {

    /**
     * Converts markdown text to HTML.
     * @param markdown The markdown string.
     * @return Rendered HTML string.
     */
    String renderToHtml(String markdown);
}
