package com.example.domain.notification.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect (VW-454).
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCmd(
    String defectId,
    String summary,
    String description,
    String severity,
    String githubIssueUrl
) implements Command {}
