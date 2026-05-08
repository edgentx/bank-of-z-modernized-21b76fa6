package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record SessionStartedEvent(String aggregateId, String tellerId, String terminalId, Instant occurredAt) implements DomainEvent {
    public SessionStartedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "session.started";
    }
}
