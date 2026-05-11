package com.example.domain.vforce360.model;

import java.time.Instant;

public record DefectReportedEvent(
    String defectId,
    String project,
    String severity,
    String description,
    String githubIssueUrl,
    Instant occurredAt
) {
}