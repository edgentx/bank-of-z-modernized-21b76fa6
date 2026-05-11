package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record RoutingRuleUpdatedEvent(
    String routeId,
    String ruleId,
    String newTarget,
    int ruleVersion,
    Instant occurredAt
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
        return occurredAt;
    }
}
