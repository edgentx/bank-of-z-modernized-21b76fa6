package com.example.ports.dto;

/**
 * Data Transfer Object for reporting a defect.
 * Represents the payload triggered via temporal-worker exec.
 */
public record ReportDefectCommand(
    String project,
    String title,
    String description,
    String severity
) {
    // Simple record to hold the defect details
}
