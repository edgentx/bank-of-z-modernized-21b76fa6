package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public record ScreenRenderedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    String screenId,
    String deviceType
) implements DomainEvent {

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType) {
        this("screen.rendered", aggregateId, Instant.now(), screenId, deviceType);
    }
}
