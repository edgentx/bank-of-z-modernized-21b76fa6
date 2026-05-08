package com.example.domain.defect.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Requires a GitHub URL which must be validated in the Slack body context.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description,
    String githubUrl
) implements Command {}
