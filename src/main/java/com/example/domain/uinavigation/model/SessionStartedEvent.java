package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        String navigationState,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
        // Default navigation state to HOME if valid context
        if (navigationState == null || navigationState.isBlank()) {
            navigationState = "HOME";
        }
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "session.started";
    }
}
