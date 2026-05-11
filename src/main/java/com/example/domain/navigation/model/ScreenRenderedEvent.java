package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a screen is successfully rendered.
 * Constructor signature matches the corrected implementation: (aggregateId, screenId, deviceType, layout, occurredAt).
 */
public record ScreenRenderedEvent(
        String aggregateId,
        String screenId,
        String deviceType,
        String layout,
        Instant occurredAt
) implements DomainEvent {

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
