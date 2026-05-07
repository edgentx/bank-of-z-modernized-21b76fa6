package com.example.domain.tellermemory.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller session is terminated.
 * @param sessionId The ID of the session.
 * @param tellerId The ID of the teller who owned the session.
 * @param reason The reason for termination.
 * @param occurredAt The timestamp of the event.
 */
public record SessionEndedEvent(
    String sessionId,
    String tellerId,
    String reason,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "session.ended"; }
    @Override public String aggregateId() { return sessionId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
