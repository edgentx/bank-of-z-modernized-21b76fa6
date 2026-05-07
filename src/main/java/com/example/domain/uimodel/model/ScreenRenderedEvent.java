package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a screen is successfully rendered.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    Instant occurredAt
) implements DomainEvent {
    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.screenId = screenId;
        this.deviceType = deviceType;
        this.occurredAt = occurredAt;
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
