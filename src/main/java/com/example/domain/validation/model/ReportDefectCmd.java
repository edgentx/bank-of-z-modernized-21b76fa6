package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect discovered in VForce360.
 * Triggers the creation of a GitHub issue and a Slack notification.
 */
public record ReportDefectCmd(
    String defectId,
    String description,
    String severity,
    String githubIssueUrl
) implements Command {}