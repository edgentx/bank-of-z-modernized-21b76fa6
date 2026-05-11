package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a screen layout has been successfully generated.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "screen.rendered";
    }

    // Default constructor for serialization safety if needed, though record is fine.
    public ScreenRenderedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }
}
