package com.example.domain.validation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to report a defect.
 * Part of the Validation aggregate (VW-454).
 */
public record ReportDefectCmd(
        String defectId,
        String projectId,
        String title,
        String severity,
        String githubUrl,
        Map<String, String> metadata
) implements Command {
}
