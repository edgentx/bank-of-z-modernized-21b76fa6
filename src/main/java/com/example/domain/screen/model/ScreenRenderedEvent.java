package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public class ScreenRenderedEvent implements DomainEvent {

    private final String aggregateId;
    private final String type;
    private final String screenMapId;
    private final Instant occurredAt;
    private final String screenId;
    private final String deviceType;
    private final Map<String, Object> layout;

    public ScreenRenderedEvent(String aggregateId, String type, String screenMapId, Instant occurredAt,
                               String screenId, String deviceType, Map<String, Object> layout) {
        this.aggregateId = aggregateId;
        this.type = type;
        this.screenMapId = screenMapId;
        this.occurredAt = occurredAt;
        this.screenId = screenId;
        this.deviceType = deviceType;
        this.layout = layout;
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

    public String screenMapId() {
        return screenMapId;
    }

    public String screenId() {
        return screenId;
    }

    public String deviceType() {
        return deviceType;
    }

    public Map<String, Object> layout() {
        return layout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenRenderedEvent that = (ScreenRenderedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) &&
                Objects.equals(type, that.type) &&
                Objects.equals(screenMapId, that.screenMapId) &&
                Objects.equals(occurredAt, that.occurredAt) &&
                Objects.equals(screenId, that.screenId) &&
                Objects.equals(deviceType, that.deviceType) &&
                Objects.equals(layout, that.layout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, type, screenMapId, occurredAt, screenId, deviceType, layout);
    }
}