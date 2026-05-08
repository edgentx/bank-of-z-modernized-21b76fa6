package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect discovered via VForce360.
 * Triggered by Temporal Worker (VW-454).
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        Severity severity,
        Map<String, String> metadata
) implements Command {

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public ReportDefectCmd {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }
    }
}