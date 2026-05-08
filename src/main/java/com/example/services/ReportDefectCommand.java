package com.example.services;

/**
 * Command object for defect reporting.
 */
public class ReportDefectCommand {
    private final String defectId;
    private final String description;
    private final String severity;

    public ReportDefectCommand(String defectId, String description, String severity) {
        this.defectId = defectId;
        this.description = description;
        this.severity = severity;
    }

    public String getDefectId() { return defectId; }
    public String getDescription() { return description; }
    public String getSeverity() { return severity; }
}
