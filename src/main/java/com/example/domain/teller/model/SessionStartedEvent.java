package com.example.domain.teller.model;

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
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
    }
    public SessionStartedEvent(String aggregateId, String tellerId, String terminalId) {
        this(aggregateId, tellerId, terminalId, Instant.now());
    }
    @Override public String type() { return "teller.session.started"; }
    @Override public String aggregateId() { return aggregateId; }
}
