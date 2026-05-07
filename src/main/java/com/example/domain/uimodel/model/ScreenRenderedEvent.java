package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ScreenRenderedEvent(
    String screenId,
    String layoutJson,
    String layoutContentType,
    Instant occurredAt
) implements DomainEvent {
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
