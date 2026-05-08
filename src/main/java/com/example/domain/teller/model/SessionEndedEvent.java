package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller session is terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {

    public SessionEndedEvent {
        // Defensive checks/defaults could go here
    }

    @Override
    public String type() {
        return "session.ended";
    }
}