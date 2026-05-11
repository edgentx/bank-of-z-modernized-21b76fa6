package com.example.domain.legacytransactionroute.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public record RoutingRuleUpdatedEvent(
    String aggregateId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    int ruleVersion,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "RoutingRuleUpdated";
    }
}
