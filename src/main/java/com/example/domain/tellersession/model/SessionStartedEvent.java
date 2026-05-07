package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Duration timeout,
        Instant occurredAt
) implements DomainEvent {
    public SessionStartedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(tellerId, "tellerId required");
        Objects.requireNonNull(terminalId, "terminalId required");
        Objects.requireNonNull(timeout, "timeout required");
        Objects.requireNonNull(occurredAt, "occurredAt required");
    }

    @Override
    public String type() {
        return "session.started";
    }
}
