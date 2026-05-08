package com.example.domain.defect;

import com.example.domain.shared.Command;

/**
 * Command to report a defect raised via the VForce360 PM diagnostic conversation.
 * Triggers the workflow that validates and posts to Slack.
 */
public record ReportDefectCmd(
        String defectId,
        String issueId,
        String summary
) implements Command {}