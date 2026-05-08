package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record RoutingEvaluatedEvent(
    String aggregateId,
    String transactionType,
    String targetSystem,
    String ruleVersion,
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
