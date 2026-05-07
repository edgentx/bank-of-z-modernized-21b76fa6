package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Set;

/**
 * Event emitted when a teller session is successfully started.
 * Context: S-18
 */
public record SessionStartedEvent(
    String sessionId,
    String tellerId,
    String terminalId,
    Set<String> permissions,
    String navigationState,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
