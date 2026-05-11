package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Event emitted when a screen is successfully rendered.
 * Constructor signature strictly adhered to: (id, screenId, deviceType, status, occurredAt).
 */
public record ScreenRenderedEvent(
        String aggregateId,
        String screenId,
        String deviceType,
        String status,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenRenderedEvent that = (ScreenRenderedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) &&
                Objects.equals(screenId, that.screenId) &&
                Objects.equals(deviceType, that.deviceType) &&
                Objects.equals(status, that.status) &&
                Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, screenId, deviceType, status, occurredAt);
    }
}
