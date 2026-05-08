package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * This triggers the workflow which eventually posts to Slack.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description
) implements Command {}