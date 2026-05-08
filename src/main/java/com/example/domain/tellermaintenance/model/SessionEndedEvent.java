package com.example.domain.tellermaintenance.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is successfully terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "session.ended";
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
