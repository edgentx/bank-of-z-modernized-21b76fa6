package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public record ScreenRenderedEvent(
        String aggregateId,
        String renderedOutput,
        String deviceType,
        String type,
        Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(type);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return type;
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