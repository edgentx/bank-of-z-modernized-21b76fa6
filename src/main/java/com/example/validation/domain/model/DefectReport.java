package com.example.validation.domain.model;

import java.util.Map;

public record DefectReport(
    String id,
    String title,
    String description,
    String severity,
    String project
) {
    public Map<String, Object> toGitHubIssuePayload() {
        return Map.of(
            "title", title,
            "body", description + "\n\n**Severity:** " + severity + "\n**Project:** " + project,
            "labels", new String[]{"defect", "VForce360"}
        );
    }
}
