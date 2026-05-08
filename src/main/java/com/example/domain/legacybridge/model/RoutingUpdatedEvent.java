package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record RoutingUpdatedEvent(
    String routeId,
    String newTarget,
    int newRuleVersion,
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
