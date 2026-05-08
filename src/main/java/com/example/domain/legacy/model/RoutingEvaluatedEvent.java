package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record RoutingEvaluatedEvent(
        String eventId,
        String aggregateId,
        String targetSystem,
        int ruleVersion,
        Map<String, String> context,
        Instant occurredAt
) implements DomainEvent {
    public RoutingEvaluatedEvent {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (targetSystem == null || targetSystem.isBlank()) throw new IllegalArgumentException("targetSystem required");
    }

    public static RoutingEvaluatedEvent create(String aggregateId, String targetSystem, int ruleVersion, Map<String, String> context) {
        return new RoutingEvaluatedEvent(
                UUID.randomUUID().toString(),
                aggregateId,
                targetSystem,
                ruleVersion,
                context,
                Instant.now()
        );
    }

    @Override
    public String type() {
        return "routing.evaluated";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}