package com.example.domain.defect.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to report a defect (triggered by VForce360).
 * Part of S-FB-1.
 */
public record ReportDefectCommand(
        String projectId,
        String title,
        String description,
        Severity severity,
        String component,
        Map<String, String> metadata
) implements Command {

    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }

    public ReportDefectCommand {
        if (projectId == null || projectId.isBlank()) throw new IllegalArgumentException("projectId required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title required");
    }
}