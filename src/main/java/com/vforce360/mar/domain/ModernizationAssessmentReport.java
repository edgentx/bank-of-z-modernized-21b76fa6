package com.vforce360.mar.domain;

import java.util.UUID;

public class ModernizationAssessmentReport {
    private UUID id;
    private String rawJsonContent;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRawJsonContent() {
        return rawJsonContent;
    }

    public void setRawJsonContent(String rawJsonContent) {
        this.rawJsonContent = rawJsonContent;
    }
}