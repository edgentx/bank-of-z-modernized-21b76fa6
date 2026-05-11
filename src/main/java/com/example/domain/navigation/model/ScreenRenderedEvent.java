package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a screen is successfully rendered.
 */
public record ScreenRenderedEvent(
        String aggregateId,
        String screenId,
        String deviceType,
        String layout,
        Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        // Ensure unique event ID if needed, though aggregateId is often sufficient for the aggregate root
        // Ensure immutability and valid timestamps
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
        if (occurredAt == null) occurredAt = Instant.now();
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
