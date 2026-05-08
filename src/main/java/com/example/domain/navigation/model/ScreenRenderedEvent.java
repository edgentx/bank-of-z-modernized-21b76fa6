package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public class ScreenRenderedEvent implements DomainEvent {

    private final String aggregateId;
    private final String screenId;
    private final String deviceType;
    private final Instant occurredAt;
    private final String status;
    private final String message;
    private final Map<String, Object> layoutSnapshot;

    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, Instant occurredAt, String status, String message, Map<String, Object> layoutSnapshot) {
        this.aggregateId = aggregateId;
        this.screenId = screenId;
        this.deviceType = deviceType;
        this.occurredAt = occurredAt;
        this.status = status;
        this.message = message;
        this.layoutSnapshot = layoutSnapshot;
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

    public String getScreenId() { return screenId; }
    public String getDeviceType() { return deviceType; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public Map<String, Object> getLayoutSnapshot() { return layoutSnapshot; }
}
