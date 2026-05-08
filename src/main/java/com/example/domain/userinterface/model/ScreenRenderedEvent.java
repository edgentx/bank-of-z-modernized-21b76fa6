package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public class ScreenRenderedEvent implements DomainEvent {
    private final String screenMapId;
    private final String screenId;
    private final String deviceType;
    private final Instant occurredAt;

    public ScreenRenderedEvent(String screenMapId, String screenId, String deviceType, Instant occurredAt) {
        this.screenMapId = screenMapId;
        this.screenId = screenId;
        this.deviceType = deviceType;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "screen.rendered";
    }

    @Override
    public String aggregateId() {
        return screenMapId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String screenMapId() { return screenMapId; }
    public String screenId() { return screenId; }
    public String deviceType() { return deviceType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenRenderedEvent that = (ScreenRenderedEvent) o;
        return Objects.equals(screenMapId, that.screenMapId) &&
                Objects.equals(screenId, that.screenId) &&
                Objects.equals(deviceType, that.deviceType) &&
                Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screenMapId, screenId, deviceType, occurredAt);
    }
}