package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

/**
 * Domain event emitted when a screen layout is successfully rendered.
 */
public class ScreenRenderedEvent implements DomainEvent {

    private final String eventId;
    private final String eventType;
    private final String aggregateId;
    private final Instant occurredAt;
    private final String screenId;
    private final String deviceType;
    private final Map<String, Object> layout;

    // Constructor signature matching the compiler error requirements
    public ScreenRenderedEvent(String eventId, String eventType, String aggregateId, Instant occurredAt, 
                               String screenId, String deviceType, Map<String, Object> layout) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.occurredAt = occurredAt;
        this.screenId = screenId;
        this.deviceType = deviceType;
        this.layout = layout;
    }

    @Override
    public String type() {
        return eventType;
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
    public Map<String, Object> layout() { return layout; }
}
