package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a routing rule is successfully updated.
 */
public record RoutingRuleUpdatedEvent(
        String eventId,
        String routeId,
        String ruleId,
        String newTarget,
        int newVersion,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {
    public RoutingRuleUpdatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

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
        return occurredAt;
    }
}
