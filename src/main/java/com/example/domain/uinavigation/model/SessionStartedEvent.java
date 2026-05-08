package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a Teller Session is successfully initiated.
 */
public record SessionStartedEvent(
        String aggregateId,
        String tellerId,
        String terminalId,
        String operationalContext,
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
