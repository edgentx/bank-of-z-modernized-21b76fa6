package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is terminated.
 * Context: Story S-20 (user-interface-navigation).
 */
public record SessionEndedEvent(
    String type,
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    public SessionEndedEvent(String aggregateId, Instant occurredAt) {
        this("session.ended", aggregateId, occurredAt);
    }
}
