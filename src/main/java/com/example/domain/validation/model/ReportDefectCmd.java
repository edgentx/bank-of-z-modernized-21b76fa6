package com.example.domain.validation.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Carries the necessary information to construct a GitHub URL and notify Slack.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description
) implements Command {}