package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect discovered in the system.
 * Triggers the Temporal workflow which eventually posts to Slack.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String severity,
    String component
) implements Command {}
