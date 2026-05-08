package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a routing decision has been made.
 * Story S-23.
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
}
