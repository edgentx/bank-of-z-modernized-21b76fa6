package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the defect reporting workflow.
 * In the context of VW-454, this corresponds to the temporal-worker exec trigger.
 */
public record ReportDefectCommand(String defectId) implements Command {
    public ReportDefectCommand {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }
    }
}