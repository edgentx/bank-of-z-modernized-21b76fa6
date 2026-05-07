package com.example.domain.validation;

/**
 * Command object representing the request to report a defect.
 * This is the input for our validation logic.
 */
public record ReportDefectCommand(
    String title,
    String description,
    String slackChannel
) {
    // Simple record for command data
}
