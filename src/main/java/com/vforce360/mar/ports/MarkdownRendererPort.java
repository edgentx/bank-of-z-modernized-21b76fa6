package com.vforce360.mar.ports;

/**
 * Port for rendering text to HTML.
 * Abstracts the specific markdown library used.
 */
public interface MarkdownRendererPort {
    
    /**
     * Renders the given markdown content to HTML.
     * @param markdown The markdown string.
     * @return HTML string.
     */
    String render(String markdown);
}