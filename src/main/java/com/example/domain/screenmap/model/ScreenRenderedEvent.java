package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a screen layout is successfully rendered.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    String layoutName,
    Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        // Defensive copy/validation if necessary, though records handle finality
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
