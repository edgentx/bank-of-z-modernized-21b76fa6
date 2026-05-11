package com.example.domain.defect;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event published when a defect is successfully reported.
 */
public record DefectReportedEvent(
    String defectId,
    String githubUrl,
    Instant occurredAt
) implements DomainEvent {
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
