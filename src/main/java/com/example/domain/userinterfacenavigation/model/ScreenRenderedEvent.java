package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    Instant occurredAt,
    Map<String, Object> layout
) implements DomainEvent {
    @Override
    public String type() {
        return "screen.rendered";
    }
}