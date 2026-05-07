package com.vforce360.model;

/**
 * Entity representing the Modernization Assessment Report.
 * Contains structured data that must be rendered into Markdown/HTML for display.
 */
public class AssessmentReport {
    private String id;
    private String rawContent; // e.g. markdown or structured JSON
    private String format;      // e.g. "MARKDOWN", "JSON"

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRawContent() { return rawContent; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}
