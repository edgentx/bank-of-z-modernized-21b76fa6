package com.vforce360.mar.model;

/**
 * Entity representing the Modernization Assessment Report.
 * Corresponds to the 'brownfield project with MAR generated'.
 */
public class ModernizationAssessmentReport {
    private String projectId;
    private String rawContent;

    public ModernizationAssessmentReport() {}

    public ModernizationAssessmentReport(String projectId, String rawContent) {
        this.projectId = projectId;
        this.rawContent = rawContent;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }
}