package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a screen has been successfully rendered.
 */
public record ScreenRenderedEvent(
        String aggregateId,
        String deviceType,
        String presentationLayout,
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

    public ScreenRenderedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(deviceType, "deviceType cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}