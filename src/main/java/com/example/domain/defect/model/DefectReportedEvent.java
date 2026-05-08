package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a defect is successfully reported.
 */
public record DefectReportedEvent(
    String eventId,
    String defectId,
    String projectId,
    String title,
    String severity,
    String githubUrl,
    Instant occurredAt
) implements DomainEvent {
    public DefectReportedEvent(String defectId, String projectId, String title, String severity, String githubUrl, Instant occurredAt) {
        this(UUID.randomUUID().toString(), defectId, projectId, title, severity, githubUrl, occurredAt);
    }

    @Override public String type() { return "DefectReported"; }
    @Override public String aggregateId() { return defectId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
