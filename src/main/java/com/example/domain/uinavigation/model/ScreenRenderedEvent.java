package com.example.domain.uinavigation.model;

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
    Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        // Defensive copy/normalization could go here
    }

    // Explicit constructor to allow omitting UUID in tests/usage if needed, 
    // though record is usually fine.
    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType) {
        this(aggregateId, screenId, deviceType, Instant.now());
    }

    @Override
    public String type() {
        return "screen.rendered";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
