package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a screen layout is successfully generated.
 */
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
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenRenderedEvent that = (ScreenRenderedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) &&
                Objects.equals(screenId, that.screenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, screenId);
    }
}