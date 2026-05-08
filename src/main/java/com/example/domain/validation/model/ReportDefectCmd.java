package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect identified in the VForce360 system.
 * This includes the logic to format the message for Slack integration.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String severity,
    String githubIssueUrl
) implements Command {}
