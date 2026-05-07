package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a session is started.
 */
public record SessionInitiatedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {

    public SessionInitiatedEvent(String aggregateId, String tellerId) {
        this(aggregateId, tellerId, Instant.now());
    }

    @Override
    public String type() {
        return "session.initiated";
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
