package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a screen layout is successfully generated.
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
