package com.example.domain.vforce360;

import com.example.domain.shared.Command;

/**
 * Command to trigger the reporting of a defect to GitHub and Slack.
 */
public record ReportDefectCommand(
    String defectId,
    String title,
    String description
) implements Command {}