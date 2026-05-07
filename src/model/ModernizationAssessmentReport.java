package com.vforce360.model;

import java.util.Map;

/**
 * Entity representing the Modernization Assessment Report.
 * Corresponds to the defect scenario where raw JSON was displayed.
 */
public class ModernizationAssessmentReport {
    
    private String projectId;
    private String title;
    private Map<String, Object> content; // The raw data structure
    private String status; // e.g., GENERATED, REVIEWED

    // Default constructor for serialization frameworks
    public ModernizationAssessmentReport() {}

    public ModernizationAssessmentReport(String projectId, String title, Map<String, Object> content, String status) {
        this.projectId = projectId;
        this.title = title;
        this.content = content;
        this.status = status;
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

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
