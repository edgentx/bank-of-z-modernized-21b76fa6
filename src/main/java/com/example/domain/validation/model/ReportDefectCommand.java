package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Typically triggered by an external workflow or user action.
 */
public record ReportDefectCommand(
    String defectId,
    String description,
    String githubIssueUrl
) implements Command {}
