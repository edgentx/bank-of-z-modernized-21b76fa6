package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Set;

public record RoutingEvaluatedEvent(
        String aggregateId,
        String targetSystem,
        Set<String> appliedRules,
        int version,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "routing.evaluated";
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