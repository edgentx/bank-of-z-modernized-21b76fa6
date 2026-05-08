package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect detected during validation or reconciliation.
 * Triggers a workflow that posts to GitHub and Slack.
 */
public record ReportDefectCmd(
    String projectId,
    String severity,
    String component,
    String title,
    String description
) implements Command {}
