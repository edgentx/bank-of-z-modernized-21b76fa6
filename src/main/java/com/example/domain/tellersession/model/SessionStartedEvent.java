package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(String sessionId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
    public SessionStartedEvent {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (occurredAt == null) occurredAt = Instant.now();
    }
    @Override public String type() { return "session.started"; }
    @Override public String aggregateId() { return sessionId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
