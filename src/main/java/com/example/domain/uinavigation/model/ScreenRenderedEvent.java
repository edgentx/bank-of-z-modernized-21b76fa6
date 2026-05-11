package com.example.domain.uinavigation.model;

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

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, Instant occurredAt) {
        this(
                aggregateId,
                screenId,
                deviceType,
                "LAYOUT_" + screenId + "_" + deviceType, // Simplified layout generation
                occurredAt
        );
    }

    @Override
    public String type() {
        return "screen.rendered";
    }
}
