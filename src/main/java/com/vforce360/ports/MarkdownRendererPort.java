package com.vforce360.ports;

/**
 * Port interface for rendering Markdown content to HTML.
 * Used by the MAR Service to ensure clean separation of concerns.
 */
public interface MarkdownRendererPort {

    /**
     * Converts Markdown source text into HTML.
     *
     * @param markdown The Markdown source.
     * @return The rendered HTML.
     */
    String renderToHtml(String markdown);
}