package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Used to trigger the defect reporting workflow which includes Slack notifications.
 */
public record ReportDefectCommand(
    String defectId,
    String severity,
    String githubUrl
) implements Command {}
