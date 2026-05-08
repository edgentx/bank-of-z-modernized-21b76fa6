package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Set;

public record SessionStartedEvent(
    String sessionId,
    String tellerId,
    String terminalId,
    Set<String> roles,
    String operationalContext,
    Instant occurredAt
) implements DomainEvent {
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
