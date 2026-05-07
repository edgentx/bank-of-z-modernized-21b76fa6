package com.vforce360.model;

/**
 * Entity representing a Modernization Assessment Report.
 * In a real scenario, this might map to a MongoDB or DB2 table.
 */
public class ModernizationAssessmentReport {

    private String projectId;
    private String rawMarkdown;
    private String status; // e.g., GENERATED, APPROVED

    public ModernizationAssessmentReport() {}

    public ModernizationAssessmentReport(String projectId, String rawMarkdown, String status) {
        this.projectId = projectId;
        this.rawMarkdown = rawMarkdown;
        this.status = status;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getRawMarkdown() {
        return rawMarkdown;
    }

    public void setRawMarkdown(String rawMarkdown) {
        this.rawMarkdown = rawMarkdown;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}