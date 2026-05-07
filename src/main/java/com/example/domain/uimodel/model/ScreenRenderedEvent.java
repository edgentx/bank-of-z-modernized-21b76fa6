package com.example.domain.uimodel.model;

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
    public ScreenRenderedEvent {
        // Ensure occurredAt is not null for the record
        if (occurredAt == null) occurredAt = Instant.now();
    }
    @Override public String type() { return "screen.rendered"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
