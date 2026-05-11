package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a screen map is successfully rendered.
 */
public record ScreenRenderedEvent(
        String aggregateId,
        String screenId,
        String deviceType,
        String layout,
        Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        // Ensure non-null for database/event store safety
        if (aggregateId == null) aggregateId = UUID.randomUUID().toString();
    }

    @Override
    public String type() {
        return "screen.rendered";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}