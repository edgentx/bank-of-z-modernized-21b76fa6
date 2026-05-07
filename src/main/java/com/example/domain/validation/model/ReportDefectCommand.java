package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect, triggered by Temporal workflow.
 * Contains the context needed to generate a GitHub issue link.
 */
public record ReportDefectCommand(
    String defectId,
    String projectId,
    String severity,
    String title
) implements Command {}
