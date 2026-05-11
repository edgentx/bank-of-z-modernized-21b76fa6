package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event recorded when a Teller Session is initialized (Authenticated).
 * Used to satisfy the "A teller must be authenticated" invariant.
 */
public record SessionInitializedEvent(
        String eventId,
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {

    public SessionInitializedEvent(String aggregateId, String tellerId, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, tellerId, occurredAt);
    }

    @Override
    public String type() {
        return "session.initialized";
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