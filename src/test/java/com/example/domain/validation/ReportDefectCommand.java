package com.example.domain.validation;

import com.example.domain.shared.Command;

/**
 * Command object representing the request to report a defect.
 * Implements the shared Command interface for consistency within the domain layer.
 */
public record ReportDefectCommand(
    String title,
    String description,
    String slackChannel
) implements Command {
}