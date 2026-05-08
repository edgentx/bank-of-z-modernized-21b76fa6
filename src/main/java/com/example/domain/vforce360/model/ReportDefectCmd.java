package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Triggers the creation of a GitHub issue and a Slack notification.
 */
public record ReportDefectCmd(
        String defectId,
        String repository,
        String title,
        String body
) implements Command {}
