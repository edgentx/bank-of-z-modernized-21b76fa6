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
    public SessionStartedEvent {
        // Defensive defaults just in case
        if (aggregateId == null) aggregateId = UUID.randomUUID().toString();
    }

    @Override
    public String type() {
        return "session.started";
    }
}