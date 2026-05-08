package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a routing decision has been successfully evaluated.
 * Consolidated into domain.legacybridge.model.
 */
public record RoutingEvaluatedEvent(
        String aggregateId,
        String transactionType,
        String targetSystem,
        Map<String, Object> payload,
        int rulesVersion,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "routing.evaluated";
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