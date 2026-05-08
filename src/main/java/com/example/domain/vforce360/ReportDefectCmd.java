package com.example.domain.vforce360;

import com.example.domain.shared.Command;

/**
 * Command to report a defect to GitHub and notify Slack.
 */
public record ReportDefectCmd(
    String defectId,
    String title,
    String description
) implements Command {}
