package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event emitted when a Teller Session is successfully started.
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