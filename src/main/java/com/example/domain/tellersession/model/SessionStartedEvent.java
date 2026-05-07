package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller successfully starts a session.
 * Triggers state updates in downstream projection (MongoDB VForce360).
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "session.started";
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