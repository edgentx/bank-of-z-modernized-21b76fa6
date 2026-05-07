package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a screen is successfully rendered.
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenRenderedEvent that = (ScreenRenderedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) && 
               Objects.equals(deviceType, that.deviceType) && 
               Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, deviceType, occurredAt);
    }
}