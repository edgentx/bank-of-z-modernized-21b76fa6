package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Event emitted when a transaction route has been successfully evaluated.
 * S-23: LegacyTransactionRoute Event.
 */
public record RoutingEvaluatedEvent(
    String eventId,
    String aggregateId,
    String transactionType,
    String targetSystem, // "MODERN" or "LEGACY"
    Map<String, Object> payload,
    int appliedRuleVersion,
    Instant occurredAt
) implements DomainEvent {
    public RoutingEvaluatedEvent(String aggregateId, String transactionType, String targetSystem, Map<String, Object> payload, int appliedRuleVersion) {
        this(UUID.randomUUID().toString(), aggregateId, transactionType, targetSystem, payload, appliedRuleVersion, Instant.now());
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
