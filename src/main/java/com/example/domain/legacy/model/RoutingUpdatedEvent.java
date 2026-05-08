package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record RoutingUpdatedEvent(
    String aggregateId,
    String type,
    Instant occurredAt,
    String routeId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    int rulesVersion
) implements DomainEvent {
    public RoutingUpdatedEvent(String routeId, String ruleId, String newTarget, Instant effectiveDate, int rulesVersion) {
        this(routeId, "RoutingUpdated", Instant.now(), routeId, ruleId, newTarget, effectiveDate, rulesVersion);
    }
}
