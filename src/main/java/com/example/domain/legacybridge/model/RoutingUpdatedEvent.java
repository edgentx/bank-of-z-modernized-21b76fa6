package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public class RoutingUpdatedEvent implements DomainEvent {
    private final String aggregateId;
    private final String newTarget;
    private final Instant effectiveDate;
    private final Instant occurredAt;

    public RoutingUpdatedEvent(String aggregateId, String newTarget, Instant effectiveDate, Instant occurredAt) {
        this.aggregateId = aggregateId;
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
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String newTarget() {
        return newTarget;
    }

    public Instant effectiveDate() {
        return effectiveDate;
    }
}
