package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Set;

public record SessionStartedEvent(
        String sessionId,
        String tellerId,
        String terminalId,
        Set<String> activeOperations,
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