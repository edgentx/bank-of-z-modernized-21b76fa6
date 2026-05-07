package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Duration;
import java.time.Instant;

/**
 * Event representing the creation/start of a session.
 * Used here primarily to hydrate the aggregate in tests.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        Instant occurredAt,
        Duration timeoutDuration
) implements DomainEvent {
    @Override
    public String type() {
        return "session.started";
    }
}