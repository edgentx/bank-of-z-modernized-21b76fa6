package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ScreenRenderedEvent(
        String eventId,
        String aggregateId,
        String screenId,
        String renderedLayout,
        String deviceType,
        Instant occurredAt
) implements DomainEvent {
    public ScreenRenderedEvent(String aggregateId, String screenId, String layout, String deviceType, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, screenId, layout, deviceType, occurredAt);
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