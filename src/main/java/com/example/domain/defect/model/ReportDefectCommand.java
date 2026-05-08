package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the defect reporting workflow.
 * This is the input for the Temporal activity/worker.
 */
public record ReportDefectCommand(
        String defectId,
        String title,
        String description,
        String severity,
        String projectId
) implements Command {}
