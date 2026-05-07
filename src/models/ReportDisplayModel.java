package com.vforce360.app.models;

/**
 * DTO representing the data to be displayed in the MAR review section.
 */
public class ReportDisplayModel {
    
    private final String projectId;
    private final String renderedHtml;

    public ReportDisplayModel(String projectId, String renderedHtml) {
        this.projectId = projectId;
        this.renderedHtml = renderedHtml;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getRenderedHtml() {
        return renderedHtml;
    }
}
