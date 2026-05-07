package com.example.domain.report.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (e.g., from PM diagnostic tools).
 * This triggers a workflow that creates a GitHub issue and notifies Slack.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String severity
) implements Command {}