package com.example.domain.tellermemory.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event representing the start of a session. Used here for state hydration in tests.
 */
public record SessionStartedEvent(
    String sessionId,
    String tellerId,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "session.started"; }
    @Override public String aggregateId() { return sessionId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
