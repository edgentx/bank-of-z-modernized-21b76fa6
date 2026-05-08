package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a routing decision has been successfully evaluated.
 */
public record RoutingEvaluatedEvent(String aggregateId, String transactionType, String targetSystem, int ruleVersion, Instant occurredAt) implements DomainEvent {
    public RoutingEvaluatedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(transactionType, "transactionType cannot be null");
        Objects.requireNonNull(targetSystem, "targetSystem cannot be null");
    }

    @Override
    public String type() {
        return "routing.evaluated";
    }
}