package com.example.domain.defect;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a defect is successfully reported to the external system.
 */
public record DefectReportedEvent(
        String eventId,
        String defectId,
        String issueId,
        String gitHubUrl,
        Instant occurredAt
) implements DomainEvent {
    public DefectReportedEvent(String defectId, String issueId, String gitHubUrl, Instant occurredAt) {
        this(UUID.randomUUID().toString(), defectId, issueId, gitHubUrl, occurredAt);
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return defectId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
