package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record ScreenRenderedEvent(
        String aggregateId,
        DeviceType deviceType,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "ScreenRenderedEvent";
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
