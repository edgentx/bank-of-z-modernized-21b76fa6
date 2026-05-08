package com.example.domain.validation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect.
 * Triggered via Temporal workflow (temporal-worker exec).
 */
public record ReportDefectCmd(
        String defectId,
        String title,
        String description,
        String githubUrl,
        Map<String, String> metadata
) implements Command {
}
