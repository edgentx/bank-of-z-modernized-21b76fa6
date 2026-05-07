package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public record ScreenRenderedEvent(
        String aggregateId,
        String screenId,
        String deviceType,
        String layout,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "screen.rendered";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
