package com.example.domain.userinterface.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a screen layout is successfully generated.
 * Part of S-21: Implement RenderScreenCmd on ScreenMap.
 */
public class ScreenRenderedEvent implements DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final String deviceType;
    private final String screenId;
    private final String layoutMetadata; // Simplified representation of the layout
    private final Instant occurredAt;

    public ScreenRenderedEvent(String aggregateId, String deviceType, String screenId, String layoutMetadata, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.deviceType = deviceType;
        this.screenId = screenId;
        this.layoutMetadata = layoutMetadata;
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

    public String eventId() { return eventId; }
    public String deviceType() { return deviceType; }
    public String screenId() { return screenId; }
    public String layoutMetadata() { return layoutMetadata; }
}
