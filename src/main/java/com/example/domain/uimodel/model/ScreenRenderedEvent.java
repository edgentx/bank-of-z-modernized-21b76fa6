package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

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
        this(UUID.randomUUID().toString(), aggregateId, screenId, deviceType, occurredAt);
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
