package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public class ScreenRenderedEvent implements DomainEvent {
    private final String aggregateId;
    private final String deviceType;
    private final String format;
    private final Map<String, Object> layout;
    private final Instant occurredAt;

    public ScreenRenderedEvent(String aggregateId, String deviceType, String format, Map<String, Object> layout, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.deviceType = deviceType;
        this.format = format;
        this.layout = layout;
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

    public String deviceType() {
        return deviceType;
    }

    public String format() {
        return format;
    }

    public Map<String, Object> layout() {
        return layout;
    }
}