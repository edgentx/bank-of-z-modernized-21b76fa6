package com.example.domain.vforce360.model;

import java.util.Objects;

/**
 * Command to report a defect.
 * Corresponds to the 'Trigger _report_defect' step.
 */
public record ReportDefectCommand(String defectId, String description) {
    public ReportDefectCommand {
        if (defectId == null || defectId.isBlank()) throw new IllegalArgumentException("defectId required");
    }
}