package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SessionLockedEvent(
        String aggregateId,
        String reason,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "teller.session.locked";
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
