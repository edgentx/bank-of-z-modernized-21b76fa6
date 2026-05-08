package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Domain event emitted when routing rules have been successfully evaluated.
 */
public record RoutingEvaluatedEvent(
        String aggregateId,
        String transactionType,
        String targetSystem,
        int ruleVersion,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "routing.evaluated";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
