package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect detected in the VForce360 PM diagnostic conversation.
 * Triggered via temporal-worker exec.
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String githubUrl // The URL to the GitHub issue (e.g., https://github.com/...) to be embedded in Slack.
) implements Command {}
