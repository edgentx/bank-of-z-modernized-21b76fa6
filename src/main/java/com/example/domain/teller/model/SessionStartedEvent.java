package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a Teller Session is successfully started.
 */
public record SessionStartedEvent(
    String sessionId,
    String tellerId,
    String terminalId,
    String navigationState,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "session.started"; }
    @Override public String aggregateId() { return sessionId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
