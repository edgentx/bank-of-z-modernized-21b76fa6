package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public record ScreenRenderedEvent(
    String eventId,
    String aggregateId,
    String screenId,
    String deviceType,
    String generatedLayout,
    Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, String generatedLayout, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, screenId, deviceType, generatedLayout, occurredAt);
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
