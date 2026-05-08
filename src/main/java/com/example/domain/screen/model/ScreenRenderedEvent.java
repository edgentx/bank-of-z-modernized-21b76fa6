package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public record ScreenRenderedEvent(
    String type,
    String aggregateId,
    String screenId,
    String deviceType,
    Instant occurredAt,
    String renderedLayout,
    Map<String, Object> context
) implements DomainEvent {

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, Instant occurredAt, String renderedLayout, Map<String, Object> context) {
        this("screen.rendered", aggregateId, screenId, deviceType, occurredAt, renderedLayout, context);
    }

    @Override
    public String type() {
        return type;
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
