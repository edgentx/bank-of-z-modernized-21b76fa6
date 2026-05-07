package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    String navigationState,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }
}
