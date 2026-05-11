package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event recorded when a Session is locked due to security or context violations.
 * Used to satisfy the "Navigation state must accurately reflect context" invariant.
 */
public record SessionLockedEvent(
        String eventId,
        String aggregateId,
        String reason,
        Instant occurredAt
) implements DomainEvent {

    public SessionLockedEvent(String aggregateId, String reason, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, reason, occurredAt);
    }

    @Override
    public String type() {
        return "session.locked";
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