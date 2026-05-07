package com.vforce360.mar.models;

/**
 * Domain model representing a Modernization Assessment Report.
 * This class acts as the DTO for the MAR data.
 */
public class ModernizationAssessmentReport {

    private String projectId;
    private String title;
    // In the actual defect, the content was stored as raw JSON structure or intended to be MD
    private String rawMarkdownContent; 
    private String status;

    // Constructors
    public ModernizationAssessmentReport() {}

    public ModernizationAssessmentReport(String projectId, String title, String rawMarkdownContent, String status) {
        this.projectId = projectId;
        this.title = title;
        this.rawMarkdownContent = rawMarkdownContent;
        this.status = status;
    }

    // Getters and Setters
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRawMarkdownContent() { return rawMarkdownContent; }
    public void setRawMarkdownContent(String rawMarkdownContent) { this.rawMarkdownContent = rawMarkdownContent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
