package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a screen layout is successfully generated and rendered.
 */
public record ScreenRenderedEvent(
        String eventId,
        String aggregateId,
        String screenId,
        String deviceType,
        Instant occurredAt
) implements DomainEvent {
    public ScreenRenderedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, Instant occurredAt) {
        this(null, aggregateId, screenId, deviceType, occurredAt);
    }

    @Override
    public String type() {
        return "screen.rendered";
    }
}
