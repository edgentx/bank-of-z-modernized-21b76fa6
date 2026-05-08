package com.example.domain.tellermSession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record SessionStartedEvent(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
