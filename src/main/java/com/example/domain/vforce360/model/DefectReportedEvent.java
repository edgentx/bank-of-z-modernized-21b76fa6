package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record DefectReportedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    String defectId,
    String severity,
    String githubIssueUrl
) implements DomainEvent {
    public DefectReportedEvent(String defectId, String severity, String githubIssueUrl, Instant occurredAt) {
        this("DefectReportedEvent", defectId, occurredAt, defectId, severity, githubIssueUrl);
    }
}