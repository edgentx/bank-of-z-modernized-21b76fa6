package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Triggers the workflow of creating a GitHub issue and notifying Slack.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description,
    String severity
) implements Command {}
