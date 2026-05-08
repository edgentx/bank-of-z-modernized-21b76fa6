package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect identified during reconciliation.
 * Contains the necessary context to log a GitHub issue and notify Slack.
 */
public record ReportDefectCmd(
    String batchId,
    String reason,
    String githubIssueUrl
) implements Command {}
