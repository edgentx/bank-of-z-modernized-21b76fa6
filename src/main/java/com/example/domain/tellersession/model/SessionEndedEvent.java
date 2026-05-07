package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a Teller Session is terminated.
 */
public record SessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.ended";
    }
}
