package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record RoutingEvaluatedEvent(
    String eventId,
    String aggregateId,
    String transactionType,
    String targetSystem,
    Instant occurredAt
) implements DomainEvent {
    public RoutingEvaluatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }
    @Override public String type() { return "routing.evaluated"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
