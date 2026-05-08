package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public record RoutingEvaluatedEvent(String routeId, String target, int version, Map<String, String> payload, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "RoutingEvaluatedEvent";
    }

    @Override
    public String aggregateId() {
        return routeId();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt();
    }
}
