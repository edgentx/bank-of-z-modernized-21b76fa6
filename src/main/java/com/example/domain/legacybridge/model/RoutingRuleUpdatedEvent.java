package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record RoutingRuleUpdatedEvent(
    String aggregateId,
    String ruleId,
    String newTarget,
    int ruleVersion,
    Instant effectiveDate,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "routing.updated";
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
