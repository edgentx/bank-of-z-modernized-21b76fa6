package com.example.steps;

import com.example.domain.shared.Command;

/**
 * Command object to initiate the defect reporting workflow.
 * Part of the temporal workflow trigger payload.
 */
public record ReportDefectCmd(
    String defectId,
    String description,
    String severity,
    String projectId
) implements Command {}
