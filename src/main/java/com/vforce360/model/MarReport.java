package com.vforce360.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MarReport {
    private String projectId;
    private String title;
    
    // The actual content stored in Mongo, potentially markdown
    private String rawContent;

    public MarReport() {}

    public MarReport(String projectId, String title, String rawContent) {
        this.projectId = projectId;
        this.title = title;
        this.rawContent = rawContent;
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getRawContent() { return rawContent; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }
}