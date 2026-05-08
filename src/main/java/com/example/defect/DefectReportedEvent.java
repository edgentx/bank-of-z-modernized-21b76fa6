package com.example.defect;

import java.time.Instant;
import java.util.UUID;

public record DefectReportedEvent(
    String defectId,
    String title,
    String severity,
    String githubIssueUrl,
    Instant occurredAt
) {
    public DefectReportedEvent(String title, String severity, String githubIssueUrl) {
        this(UUID.randomUUID().toString(), title, severity, githubIssueUrl, Instant.now());
    }
}