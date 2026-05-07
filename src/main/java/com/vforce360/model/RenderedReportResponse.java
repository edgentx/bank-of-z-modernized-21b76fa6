package com.vforce360.model;

/**
 * DTO representing the API response for the MAR review section.
 * Expected to contain formatted HTML/Markdown, not raw JSON.
 */
public class RenderedReportResponse {
    private String content; // The rendered HTML
    private String contentType; // e.g. "text/html"

    public RenderedReportResponse(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public String getContent() { return content; }
    public String getContentType() { return contentType; }
}
