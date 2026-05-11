package com.example.domain.reporting.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect discovered during operations or reconciliation.
 * This defect report triggers a workflow to log the issue to GitHub and notify via Slack.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String severity // LOW, MEDIUM, HIGH, CRITICAL
) implements Command {}