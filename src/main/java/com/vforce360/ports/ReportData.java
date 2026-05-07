package com.vforce360.ports;

import java.util.Map;

/**
 * Data transfer object for report content.
 * Used to decouple internal domain models from database entities.
 */
public class ReportData {
    private String projectId;
    private String contentJson;
    private Map<String, Object> metadata;

    // Constructors
    public ReportData() {}

    public ReportData(String projectId, String contentJson) {
        this.projectId = projectId;
        this.contentJson = contentJson;
    }

    // Getters and Setters
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
