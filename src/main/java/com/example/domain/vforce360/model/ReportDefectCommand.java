package com.example.domain.vforce360.model;

import java.util.Map;

public record ReportDefectCommand(
        String title,
        String description,
        String severity,
        String component,
        String projectId,
        Map<String, String> metadata
) {
    public ReportDefectCommand {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title cannot be null");
    }
}