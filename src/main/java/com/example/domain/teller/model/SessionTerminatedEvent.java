package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event recorded when a Session is terminated (e.g., timeout).
 * Used to satisfy the "Sessions must timeout" invariant validation.
 */
public record SessionTerminatedEvent(
        String eventId,
        String aggregateId,
        String reason,
        Instant occurredAt
) implements DomainEvent {

    public SessionTerminatedEvent(String aggregateId, String reason, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, reason, occurredAt);
    }

    @Override
    public String type() {
        return "session.terminated";
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