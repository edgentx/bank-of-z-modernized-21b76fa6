package com.vforce360.ports;

/**
 * Port interface for rendering Markdown text to HTML.
 * Abstracts the specific library used (e.g., CommonMark).
 */
public interface MarkdownRendererPort {
    /**
     * Renders the provided markdown string to HTML.
     * @param markdown The markdown content.
     * @return HTML representation.
     */
    String renderToHtml(String markdown);
}
