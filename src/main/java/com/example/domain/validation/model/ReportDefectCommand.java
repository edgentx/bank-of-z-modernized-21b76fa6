package com.example.domain.validation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect, typically triggered via Temporal workflow.
 * Contains details about the defect to be formatted into a Slack message.
 */
public record ReportDefectCommand(
        String defectId,
        String title,
        String severity,
        Map<String, Object> metadata
) implements Command {
}