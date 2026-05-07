package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public record ScreenRenderedEvent(
        String aggregateId,
        DeviceType deviceType,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "screen.rendered";
    }

    // For safety/consistency with interface definition, though record handles constructor
    public static ScreenRenderedEvent create(String screenId, DeviceType deviceType, Instant occurredAt) {
        return new ScreenRenderedEvent(screenId, deviceType, occurredAt);
    }
}
