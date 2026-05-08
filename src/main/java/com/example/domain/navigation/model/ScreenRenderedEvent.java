package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public record ScreenRenderedEvent(
    String aggregateId,
    String type,
    Instant occurredAt,
    String screenId,
    String deviceType,
    Map<String, Object> inputData
) implements DomainEvent {

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, Map<String, Object> inputData) {
        this(aggregateId, "screen.rendered", Instant.now(), screenId, deviceType, inputData);
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
