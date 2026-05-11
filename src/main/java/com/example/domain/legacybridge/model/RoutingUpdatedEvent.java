package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record RoutingUpdatedEvent(
    String aggregateId,
    String ruleId,
    String newTarget,
    int ruleVersion,
    Instant effectiveDate
) implements DomainEvent {
    @Override
    public String type() {
        return "routing.updated";
    }

    @Override
    public Instant occurredAt() {
        return effectiveDate();
    }

    @Override
    public String aggregateId() {
        return aggregateId();
    }
}
