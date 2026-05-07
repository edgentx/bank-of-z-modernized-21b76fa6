package com.example.domain.tellermgmt.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a Teller Session is successfully started.
 * Used in S-18.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "session.started";
    }

    // Constructor helper or validation could go here
}
