package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when a screen layout is successfully generated.
 */
public record ScreenRenderedEvent(
    String eventType, // "screen.rendered"
    String aggregateId,
    String screenId,
    String deviceType,
    Map<String, String> inputData,
    Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        if (eventType == null) eventType = "screen.rendered";
    }

    @Override
    public String type() {
        return eventType;
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
