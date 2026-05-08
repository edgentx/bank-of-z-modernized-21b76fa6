package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (VW-454 scenario).
 * This command triggers the workflow that creates a GitHub issue and notifies Slack.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description
) implements Command {}
