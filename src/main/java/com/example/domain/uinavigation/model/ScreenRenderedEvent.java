package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public record ScreenRenderedEvent(
    String type,
    String aggregateId,
    String screenId,
    String deviceType,
    Instant occurredAt,
    String layout,
    Map<String, Object> context
) implements DomainEvent {
    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, String layout, Map<String, Object> context) {
        this("screen.rendered", aggregateId, screenId, deviceType, Instant.now(), layout, context);
    }
}
