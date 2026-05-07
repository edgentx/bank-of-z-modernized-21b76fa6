package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record SessionStartedEvent(
        String eventId,
        String aggregateId,
        String tellerId,
        String terminalId,
        String operationalContext,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId, String operationalContext, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, tellerId, terminalId, operationalContext, occurredAt);
    }

    @Override
    public String type() {
        return "session.started";
    }
}
