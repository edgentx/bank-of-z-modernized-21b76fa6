package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to GitHub and notify via Slack.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description
) implements Command {}
