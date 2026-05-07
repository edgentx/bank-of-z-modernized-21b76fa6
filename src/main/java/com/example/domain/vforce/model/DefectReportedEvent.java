package com.example.domain.vforce.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a defect is reported to the VForce360 system.
 * Contains the GitHub issue URL for traceability.
 */
public record DefectReportedEvent(
    String aggregateId,
    String type,
    String description,
    String githubIssueUrl,
    Instant occurredAt
) implements DomainEvent {
    public DefectReportedEvent {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (type == null || type.isBlank()) throw new IllegalArgumentException("type required");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt required");
        // githubIssueUrl can be null if GitHub creation failed, but the event still must be emitted.
    }

    // Factory to simplify creation in aggregates
    public static DefectReportedEvent create(String description, String githubUrl) {
        return new DefectReportedEvent(
            UUID.randomUUID().toString(),
            "DefectReported",
            description,
            githubUrl,
            Instant.now()
        );
    }

    @Override public String type() { return type; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
