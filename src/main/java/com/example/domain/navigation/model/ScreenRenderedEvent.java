package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public record ScreenRenderedEvent(
    String type,
    String aggregateId,
    String screenId,
    Instant occurredAt
) implements DomainEvent {
    public ScreenRenderedEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(screenId);
        Objects.requireNonNull(occurredAt);
    }

    public ScreenRenderedEvent(String aggregateId, String screenId, Instant occurredAt) {
        this("screen.rendered", aggregateId, screenId, occurredAt);
    }
}
