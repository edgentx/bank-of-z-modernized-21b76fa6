package com.vforce360.domain;

import java.util.List;
import java.util.Map;

/**
 * Domain object representing the Modernization Assessment Report.
 * This data is eventually rendered into HTML/Markdown for the user.
 */
public class ModernizationAssessmentReport {
    private String projectId;
    private String title;
    private String executiveSummary;
    private List<String> keyFindings;
    private Map<String, String> technicalMetrics;

    // Constructors
    public ModernizationAssessmentReport() {}

    public ModernizationAssessmentReport(String projectId, String title, String executiveSummary, List<String> keyFindings, Map<String, String> technicalMetrics) {
        this.projectId = projectId;
        this.title = title;
        this.executiveSummary = executiveSummary;
        this.keyFindings = keyFindings;
        this.technicalMetrics = technicalMetrics;
    }

    // Getters
    public String getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getExecutiveSummary() { return executiveSummary; }
    public List<String> getKeyFindings() { return keyFindings; }
    public Map<String, String> getTechnicalMetrics() { return technicalMetrics; }
}