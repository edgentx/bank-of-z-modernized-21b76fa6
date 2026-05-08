package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record RoutingUpdatedEvent(
        String aggregateId,
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "RoutingUpdated";
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
