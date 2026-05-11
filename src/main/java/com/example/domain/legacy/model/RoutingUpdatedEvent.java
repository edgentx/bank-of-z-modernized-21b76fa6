package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record RoutingUpdatedEvent(
    String eventAggregateId,
    String ruleId,
    String newTarget,
    Instant effectiveDate,
    Instant occurredAt
) implements DomainEvent {
    public RoutingUpdatedEvent {
        // Ensure IDs are present
        if (eventAggregateId == null || eventAggregateId.isBlank()) {
            throw new IllegalArgumentException("eventAggregateId cannot be blank");
        }
    }

    @Override
    public String type() {
        return "RoutingUpdated";
    }

    @Override
    public String aggregateId() {
        return eventAggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
