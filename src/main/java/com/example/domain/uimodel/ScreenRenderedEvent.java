package com.example.domain.uimodel;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ScreenRenderedEvent(
        String aggregateId,
        String screenId,
        String deviceType,
        Instant occurredAt
) implements DomainEvent {
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