package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a screen layout is successfully generated and adapted.
 */
public record ScreenRenderedEvent(
    String type,
    String aggregateId,
    String screenId,
    String deviceType,
    String layout,
    Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, String layout, Instant occurredAt) {
        this("screen.rendered", aggregateId, screenId, deviceType, layout, occurredAt);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenRenderedEvent that = (ScreenRenderedEvent) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(aggregateId, that.aggregateId) &&
                Objects.equals(screenId, that.screenId) &&
                Objects.equals(deviceType, that.deviceType) &&
                Objects.equals(layout, that.layout) &&
                Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, aggregateId, screenId, deviceType, layout, occurredAt);
    }
}
