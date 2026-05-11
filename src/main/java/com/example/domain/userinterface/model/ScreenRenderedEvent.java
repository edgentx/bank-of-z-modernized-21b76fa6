package com.example.domain.userinterface.model;

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
    String title,
    Map<String, Object> fields
) implements DomainEvent {
    public ScreenRenderedEvent {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt cannot be null");
    }
}