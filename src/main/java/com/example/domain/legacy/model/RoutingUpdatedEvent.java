package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record RoutingUpdatedEvent(
        String aggregateId,
        String ruleId,
        String newTarget,
        int ruleVersion,
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
