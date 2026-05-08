package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when routing rules are successfully evaluated.
 */
public record RoutingEvaluatedEvent(
        String aggregateId,
        String targetSystem, // "MODERN" or "LEGACY"
        String rulesVersion,
        Map<String, Object> context,
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
