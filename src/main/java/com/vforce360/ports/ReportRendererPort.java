package com.vforce360.ports;

/**
 * Port interface for Report Rendering logic.
 */
public interface ReportRendererPort {
    String renderMarkdownToHtml(String markdownContent);
}