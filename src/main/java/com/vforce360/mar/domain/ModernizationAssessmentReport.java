package com.vforce360.mar.domain;

import java.util.UUID;

/**
 * Domain entity representing the Modernization Assessment Report.
 */
public class ModernizationAssessmentReport {

    private UUID projectId;
    private String rawContent; // The raw JSON/Markdown content

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }
}
