package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    String layoutContent,
    Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(screenId);
        Objects.requireNonNull(deviceType);
        Objects.requireNonNull(layoutContent);
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
