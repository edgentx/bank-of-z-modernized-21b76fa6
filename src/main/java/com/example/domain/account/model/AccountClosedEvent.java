package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record AccountClosedEvent(
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "AccountClosed"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
