package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect, triggering workflow execution and Slack notifications.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubIssueUrl
) implements Command {}