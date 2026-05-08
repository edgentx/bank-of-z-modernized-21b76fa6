package com.example.domain.ui.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record ScreenRenderedEvent(
    String aggregateId,
    String deviceType,
    String layout,
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
