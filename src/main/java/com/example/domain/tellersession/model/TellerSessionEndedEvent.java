package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a teller session is ended.
 */
public record TellerSessionEndedEvent(
        String type,
        String aggregateId,
        String tellerId,
        Instant occurredAt
) implements DomainEvent {
    public TellerSessionEndedEvent {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    public TellerSessionEndedEvent(String aggregateId, String tellerId) {
        this("teller.session.ended", aggregateId, tellerId, Instant.now());
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
