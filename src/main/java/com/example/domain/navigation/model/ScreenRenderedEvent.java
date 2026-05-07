package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a screen layout is successfully generated.
 */
public record ScreenRenderedEvent(
    String eventId,
    String aggregateId,
    String screenId,
    String deviceType,
    String generatedLayout,
    Instant occurredAt
) implements DomainEvent {
    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, String generatedLayout) {
        this(UUID.randomUUID().toString(), aggregateId, screenId, deviceType, generatedLayout, Instant.now());
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