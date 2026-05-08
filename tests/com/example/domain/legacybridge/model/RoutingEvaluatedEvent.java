package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record RoutingEvaluatedEvent(
    String aggregateId,
    String transactionType,
    String targetSystem,
    Map<String, Object> payload,
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
