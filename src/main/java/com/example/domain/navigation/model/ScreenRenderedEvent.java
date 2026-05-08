package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    Instant occurredAt
) implements DomainEvent {
    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType) {
        this(aggregateId, screenId, deviceType, Instant.now());
    }
    @Override public String type() { return "screen.rendered"; }
}
