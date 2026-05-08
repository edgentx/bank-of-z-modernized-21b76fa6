package com.example.domain.userinterfacenavigation.model;

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
    String layoutData,
    Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        // Ensure immutability or defaults if necessary, though record handles this.
        // We could add UUID generation here if needed for Event ID, but requirements focus on Aggregate ID.
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
