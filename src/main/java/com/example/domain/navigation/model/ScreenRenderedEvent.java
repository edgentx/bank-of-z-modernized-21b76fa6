package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a screen layout is successfully generated.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String type,
    String screenId,
    Map<String, Object> layout,
    Instant occurredAt
) implements DomainEvent {
    public ScreenRenderedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (type == null) throw new IllegalArgumentException("type required");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt required");
    }
}