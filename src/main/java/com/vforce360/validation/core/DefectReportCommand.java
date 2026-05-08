package com.vforce360.validation.core;

import java.util.UUID;

/**
 * Command object representing the intent to report a defect.
 * Used as input for the DefectReportOrchestrator.
 */
public class DefectReportCommand {

    private final String title;
    private final String description;
    private final Severity severity;
    private final String correlationId;

    public DefectReportCommand(String title, String description, Severity severity) {
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.correlationId = UUID.randomUUID().toString();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
