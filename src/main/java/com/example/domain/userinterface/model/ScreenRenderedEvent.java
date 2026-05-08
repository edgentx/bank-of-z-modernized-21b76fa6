package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record ScreenRenderedEvent(
        String screenMapId,
        String screenId,
        String deviceType,
        Map<String, String> layout,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "screen.rendered";
    }

    @Override
    public String aggregateId() {
        return screenMapId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
