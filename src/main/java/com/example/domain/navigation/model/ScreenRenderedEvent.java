package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a screen layout has been successfully rendered.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    Map<String, String> context,
    Instant occurredAt
) implements DomainEvent {
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
