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
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "screen.rendered";
    }
}