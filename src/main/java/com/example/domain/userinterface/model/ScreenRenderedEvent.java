package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a screen is successfully rendered.
 * Story S-21: RenderScreenCmd on ScreenMap.
 */
public class ScreenRenderedEvent implements DomainEvent {
    private final String aggregateId;
    private final String screenId;
    private final String deviceType;
    private final String generatedLayout;
    private final Instant occurredAt;

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, String generatedLayout, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.screenId = screenId;
        this.deviceType = deviceType;
        this.generatedLayout = generatedLayout;
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

    public String screenId() {
        return screenId;
    }

    public String deviceType() {
        return deviceType;
    }

    public String generatedLayout() {
        return generatedLayout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenRenderedEvent that = (ScreenRenderedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) &&
                Objects.equals(screenId, that.screenId) &&
                Objects.equals(deviceType, that.deviceType) &&
                Objects.equals(generatedLayout, that.generatedLayout) &&
                Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, screenId, deviceType, generatedLayout, occurredAt);
    }
}
