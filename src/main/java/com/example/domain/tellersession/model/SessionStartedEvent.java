package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredAt,
        String stateId
) implements DomainEvent {
    public SessionStartedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    public static SessionStartedEvent create(String aggregateId, String tellerId, String terminalId) {
        return new SessionStartedEvent(
                aggregateId,
                tellerId,
                terminalId,
                Instant.now(),
                UUID.randomUUID().toString()
        );
    }

    @Override
    public String type() {
        return "session.started";
    }
}