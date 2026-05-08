package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record RoutingEvaluatedEvent(
        String eventId,
        String aggregateId,
        String transactionType,
        String targetSystem,
        String ruleVersion,
        Map<String, Object> context,
        Instant occurredAt
) implements DomainEvent {
    public RoutingEvaluatedEvent(String aggregateId, String transactionType, String targetSystem, String ruleVersion, Map<String, Object> context, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, transactionType, targetSystem, ruleVersion, context, occurredAt);
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