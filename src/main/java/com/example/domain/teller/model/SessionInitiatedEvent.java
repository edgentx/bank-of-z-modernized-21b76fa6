package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a Teller Session is initiated.
 */
public record SessionInitiatedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "session.initiated";
    }
}
