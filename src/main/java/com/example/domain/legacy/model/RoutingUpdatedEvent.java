package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record RoutingUpdatedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String ruleId,
        String newTarget,
        Instant effectiveDate,
        int version
) implements DomainEvent {
    public RoutingUpdatedEvent(String aggregateId, String ruleId, String newTarget, Instant effectiveDate, int version) {
        this("routing.updated", aggregateId, Instant.now(), ruleId, newTarget, effectiveDate, version);
    }
}
