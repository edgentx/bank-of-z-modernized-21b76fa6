package com.vforce360.mar.ports;

/**
 * Port interface for rendering raw text content into a viewable format (HTML).
 * Implementations will handle Markdown parsing or similar transformations.
 */
public interface MarRendererPort {

    /**
     * Renders the provided raw content into HTML.
     * @param rawContent The raw string content (e.g., Markdown).
     * @return The rendered HTML string.
     */
    String renderMarkdown(String rawContent);
}
