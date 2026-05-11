package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to Slack.
 * Part of Story S-FB-1.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity,
    String githubIssueUrl
) implements Command {}
