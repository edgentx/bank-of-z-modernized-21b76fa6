package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public class RoutingUpdatedEvent implements DomainEvent {
    private final String routeId;
    private final String newTarget;
    private final Instant effectiveDate;
    private final Instant occurredAt;

    public RoutingUpdatedEvent(String routeId, String newTarget, Instant effectiveDate, Instant occurredAt) {
        this.routeId = routeId;
        this.newTarget = newTarget;
        this.effectiveDate = effectiveDate;
        this.occurredAt = occurredAt;
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

    public String getRouteId() {
        return routeId;
    }

    public String getNewTarget() {
        return newTarget;
    }

    public Instant getEffectiveDate() {
        return effectiveDate;
    }
}