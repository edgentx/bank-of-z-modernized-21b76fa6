package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    String renderedLayout,
    Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        // Ensure it's not null, though UUID handles string check mostly
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
    }

    @Override
    public String type() {
        return "screen.rendered";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
