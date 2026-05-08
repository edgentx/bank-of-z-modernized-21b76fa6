package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a transaction routing has been successfully evaluated.
 * Signifies the target system (Modern vs Legacy) for the transaction.
 */
public record RoutingEvaluatedEvent(
    String eventId,
    String aggregateId,
    String transactionId,
    String transactionType,
    String targetSystem,
    Instant occurredAt
) implements DomainEvent {
    public RoutingEvaluatedEvent(String aggregateId, String transactionId, String transactionType, String targetSystem, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, transactionId, transactionType, targetSystem, occurredAt);
    }

    @Override
    public String type() {
        return "routing.evaluated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
