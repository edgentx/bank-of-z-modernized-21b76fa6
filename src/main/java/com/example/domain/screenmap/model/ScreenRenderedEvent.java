package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ScreenRenderedEvent(
        String aggregateId,
        String deviceType,
        Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        // Ensure immutability/validity if needed, though record handles most
    }

    @Override
    public String type() {
        return "screen.rendered";
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