package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a defect is reported by the VForce360 PM diagnostic flow.
 */
public record DefectReportedEvent(
    String aggregateId,
    String description,
    String reporter,
    String severity,
    Instant occurredAt
) implements DomainEvent {

    public DefectReportedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(description);
        Objects.requireNonNull(reporter);
        Objects.requireNonNull(severity);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
