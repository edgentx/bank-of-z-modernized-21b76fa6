package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record ScreenRenderedEvent(
    String aggregateId,
    String type, 
    String screenLayout,
    Instant occurredAt,
    String screenId,
    String deviceType,
    Map<String, Object> contextData
) implements DomainEvent {

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
