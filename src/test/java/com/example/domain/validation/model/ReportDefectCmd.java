package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (VW-454 scenario).
 * This command triggers the workflow that eventually posts to Slack.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description
) implements Command {}
