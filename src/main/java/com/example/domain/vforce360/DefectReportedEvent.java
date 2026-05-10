package com.example.domain.vforce360;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record DefectReportedEvent(
    String eventId,
    String aggregateId,
    String title,
    String severity,
    String gitHubUrl,
    Instant occurredAt
) implements DomainEvent {
    public DefectReportedEvent(String aggregateId, String title, String severity, String gitHubUrl, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, title, severity, gitHubUrl, occurredAt);
    }
    @Override public String type() { return "DefectReported"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
