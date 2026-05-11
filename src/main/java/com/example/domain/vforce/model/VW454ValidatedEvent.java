package com.example.domain.vforce.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a defect validation is complete.
 */
public record VW454ValidatedEvent(
    String aggregateId,
    String issueId,
    String githubUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "VW454Validated";
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