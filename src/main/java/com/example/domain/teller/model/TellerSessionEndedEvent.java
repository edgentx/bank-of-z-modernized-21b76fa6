package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

public record TellerSessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    public TellerSessionEndedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "teller.session.ended";
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
