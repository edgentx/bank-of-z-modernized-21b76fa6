package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public class ScreenRenderedEvent implements DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final String screenId;
    private final String deviceType;
    private final Instant occurredAt;

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, Instant occurredAt) {
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

    public String screenId() { return screenId; }
    public String deviceType() { return deviceType; }
}
