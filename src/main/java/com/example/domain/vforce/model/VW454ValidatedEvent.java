package com.example.domain.vforce.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event published when the VForce360 validation workflow (VW-454)
 * confirms the Slack body contains the GitHub issue URL.
 */
public record VW454ValidatedEvent(
        String aggregateId,
        String githubUrl,
        String correlationId,
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