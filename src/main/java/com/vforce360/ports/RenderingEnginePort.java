package com.vforce360.ports;

/**
 * Port interface for rendering logic.
 * This abstraction allows us to swap rendering libraries (e.g., Flexmark vs CommonMark) 
 * without changing the Service or Controller logic.
 */
public interface RenderingEnginePort {
    
    /**
     * Converts a Markdown string to an HTML string.
     * 
     * @param markdown The raw markdown content.
     * @return Rendered HTML.
     */
    String convertMarkdownToHtml(String markdown);
}
