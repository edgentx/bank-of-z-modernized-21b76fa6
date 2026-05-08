package com.example.domain.validation;

import com.example.domain.shared.Command;

/**
 * Command to report a defect.
 * Contains the necessary context to generate the Slack notification.
 */
public record ReportDefectCommand(
        String defectId,
        String githubUrl,
        String targetChannel
) implements Command {}
