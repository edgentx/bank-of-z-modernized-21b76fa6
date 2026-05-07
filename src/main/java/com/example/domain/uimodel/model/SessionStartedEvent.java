package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a teller session starts successfully.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        String navigationContext,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "session.started";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public SessionStartedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
        Objects.requireNonNull(occurredAt);
    }
}
