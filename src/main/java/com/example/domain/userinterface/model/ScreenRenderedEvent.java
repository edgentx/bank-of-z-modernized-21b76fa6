package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    String layout,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "screen.rendered";
    }
}
