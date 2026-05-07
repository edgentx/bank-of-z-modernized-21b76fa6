package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a screen layout is successfully rendered.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String deviceType,
    String layout,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "screen.rendered";
    }
}
