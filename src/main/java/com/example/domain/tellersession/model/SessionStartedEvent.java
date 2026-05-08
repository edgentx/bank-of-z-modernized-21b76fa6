package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
    String aggregateId,
    String tellerId,
    String terminalId,
    Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId) {
        this(aggregateId, tellerId, terminalId, Instant.now());
    }

    @Override
    public String type() {
        return "session.started";
    }
}