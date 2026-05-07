package com.vforce360.shared.ports;

/**
 * Port interface for converting raw text content into formatted HTML.
 * This isolates the rendering engine (e.g., CommonMark) from the application logic.
 */
public interface MarkdownRendererPort {

    /**
     * Parses the input markdown (or in this defect case, JSON) and returns formatted HTML.
     *
     * @param markdownContent The raw string content (Markdown or JSON).
     * @return HTML string representation.
     */
    String renderToHtml(String markdownContent);
}
