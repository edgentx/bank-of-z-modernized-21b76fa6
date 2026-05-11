package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record RoutingUpdatedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String ruleId,
        String newTarget,
        int newVersion,
        Instant effectiveDate
) implements DomainEvent {
    public RoutingUpdatedEvent(String aggregateId, String ruleId, String newTarget, int newVersion, Instant effectiveDate) {
        this("RoutingUpdatedEvent", aggregateId, Instant.now(), ruleId, newTarget, newVersion, effectiveDate);
    }
}
