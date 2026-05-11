package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record RoutingUpdatedEvent(
    String routeId,
    String ruleId,
    String newTarget,
    int version,
    Instant effectiveDate
) implements DomainEvent {
    @Override
    public String type() {
        return "routing.updated";
    }

    @Override
    public String aggregateId() {
        return routeId;
    }

    @Override
    public Instant occurredAt() {
        return effectiveDate;
    }
}
