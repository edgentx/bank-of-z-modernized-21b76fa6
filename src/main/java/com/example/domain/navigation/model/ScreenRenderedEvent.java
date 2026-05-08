package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a screen layout is successfully generated.
 */
public record ScreenRenderedEvent(
        String aggregateId,
        String deviceType,
        Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        // Ensure immutability and non-nulls
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
        if (deviceType == null) throw new IllegalArgumentException("deviceType cannot be null");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "screen.rendered";
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
