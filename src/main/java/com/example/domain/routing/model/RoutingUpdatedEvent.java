package com.example.domain.routing.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a routing rule is successfully updated.
 */
public record RoutingUpdatedEvent(
    String aggregateId,
    String newTarget,
    int newVersion,
    Instant effectiveDate,
    Instant occurredAt
) implements DomainEvent {

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
}
