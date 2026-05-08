package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ScreenRenderedEvent(String screenId, String deviceType, int width, int height, Instant occurredAt) implements DomainEvent {
    @Override
    public String type() {
        return "screen.rendered";
    }

    @Override
    public String aggregateId() {
        return screenId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
