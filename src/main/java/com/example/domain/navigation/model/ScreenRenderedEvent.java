package com.example.domain.navigation.model;

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
        // Ensure immutability and validity if necessary
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
    }

    @Override
    public String type() {
        return "screen.rendered";
    }
}
