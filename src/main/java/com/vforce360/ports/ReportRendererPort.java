package com.vforce360.ports;

/**
 * Port interface for rendering various report formats.
 */
public interface ReportRendererPort {

    /**
     * Renders Markdown content to HTML.
     * @param markdown The raw markdown string.
     * @return Rendered HTML string.
     */
    String renderMarkdownToHtml(String markdown);
}