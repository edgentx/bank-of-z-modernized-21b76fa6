package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    Instant occurredAt
) implements DomainEvent {
    public ScreenRenderedEvent {
        // Ensure defensive copy or immutability if needed, though records are immutable
    }
    @Override public String type() { return "screen.rendered"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
