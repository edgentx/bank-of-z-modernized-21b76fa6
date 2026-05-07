package com.vforce360.mar.model;

/**
 * Data Transfer Object representing the Modernization Assessment Report.
 * In the real database, this might be mapped to MongoDB or DB2 tables.
 */
public class MarDocument {

    private String projectId;
    private String title;
    private String contentMarkdown;

    // Default constructor for frameworks/serialization
    public MarDocument() {}

    public MarDocument(String projectId, String title, String contentMarkdown) {
        this.projectId = projectId;
        this.title = title;
        this.contentMarkdown = contentMarkdown;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentMarkdown() {
        return contentMarkdown;
    }

    public void setContentMarkdown(String contentMarkdown) {
        this.contentMarkdown = contentMarkdown;
    }
}