package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record RoutingUpdatedEvent(
        String eventId,
        String aggregateId,
        String ruleId,
        String newTarget,
        int newVersion,
        Instant effectiveDate,
        Instant occurredAt
) implements DomainEvent {
    public RoutingUpdatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    @Override
    public String type() {
        return "routing.updated";
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
