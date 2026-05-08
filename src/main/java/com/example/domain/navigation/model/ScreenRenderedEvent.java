package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a screen layout is successfully generated.
 */
public class ScreenRenderedEvent implements DomainEvent {
    private final String aggregateId;
    private final String screenId;
    private final String deviceType;
    private final String layoutPayload;
    private final Instant occurredAt;

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, String layoutPayload, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.screenId = screenId;
        this.deviceType = deviceType;
        this.layoutPayload = layoutPayload;
        this.occurredAt = occurredAt;
    }

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

    public String screenId() { return screenId; }
    public String deviceType() { return deviceType; }
    public String layoutPayload() { return layoutPayload; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenRenderedEvent that = (ScreenRenderedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) &&
               Objects.equals(screenId, that.screenId) &&
               Objects.equals(deviceType, that.deviceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, screenId, deviceType);
    }
}
