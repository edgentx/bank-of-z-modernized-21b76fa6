package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a defect is reported.
 */
public record DefectReportedEvent(
    String aggregateId,
    String defectId,
    String githubUrl,
    Instant occurredAt
) implements DomainEvent {
    public DefectReportedEvent(String defectId, String githubUrl, Instant occurredAt) {
        this(UUID.randomUUID().toString(), defectId, githubUrl, occurredAt);
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
