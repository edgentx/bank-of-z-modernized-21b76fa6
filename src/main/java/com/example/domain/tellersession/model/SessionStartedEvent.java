package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant occurredOn
) implements DomainEvent {

    public SessionStartedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
    }

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public Instant occurredAt() {
        return occurredOn;
    }
}
