package com.example.domain.uimodel;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record TellerSessionEndedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "session.ended";
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
