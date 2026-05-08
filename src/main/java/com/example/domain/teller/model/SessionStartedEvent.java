package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is successfully started.
 */
public record SessionStartedEvent(String aggregateId, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }
}
