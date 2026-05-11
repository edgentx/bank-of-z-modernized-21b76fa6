package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record RoutingUpdatedEvent(
    String type,
    String aggregateId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    Instant occurredAt
) implements DomainEvent {
    public RoutingUpdatedEvent(String aggregateId, String ruleId, String newTarget, Instant effectiveDate) {
        this("RoutingUpdated", aggregateId, ruleId, newTarget, effectiveDate, Instant.now());
    }
}
