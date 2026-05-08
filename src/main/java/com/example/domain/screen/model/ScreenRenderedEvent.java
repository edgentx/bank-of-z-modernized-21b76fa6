package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a screen is successfully rendered.
 */
public class ScreenRenderedEvent implements DomainEvent {

    private final String eventId;
    private final String aggregateId;
    private final Instant occurredAt;
    private final String screenId;
    private final RenderScreenCmd.DeviceType deviceType;

    public ScreenRenderedEvent(String aggregateId, String screenId, RenderScreenCmd.DeviceType deviceType, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
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
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getScreenId() {
        return screenId;
    }

    public RenderScreenCmd.DeviceType getDeviceType() {
        return deviceType;
    }
}
