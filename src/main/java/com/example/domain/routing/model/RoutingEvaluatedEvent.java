package com.example.domain.routing.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event emitted when routing rules have been successfully evaluated.
 * Indicates the target system (e.g., MODERN or LEGACY) and the rule version used.
 */
public record RoutingEvaluatedEvent(
        String aggregateId,
        String transactionType,
        String targetSystem, // e.g., "MODERN", "LEGACY"
        int ruleVersion,
        Map<String, Object> context,
        Instant occurredAt
) implements DomainEvent {
    public RoutingEvaluatedEvent {
        // Ensure immutability and basic validation
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

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