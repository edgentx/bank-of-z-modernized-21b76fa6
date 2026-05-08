package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ScreenRenderedEvent(
        String type,
        String aggregateId,
        String screenId,
        String deviceType,
        Instant occurredAt
) implements DomainEvent {
    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, Instant occurredAt) {
        this("screen.rendered", aggregateId, screenId, deviceType, occurredAt);
    }
}