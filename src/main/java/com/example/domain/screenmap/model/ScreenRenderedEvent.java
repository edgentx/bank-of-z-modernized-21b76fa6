package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record ScreenRenderedEvent(
        String aggregateId,
        String type,
        Instant occurredAt,
        Map<String, Object> layoutData
) implements DomainEvent {
    public ScreenRenderedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(type);
        Objects.requireNonNull(occurredAt);
        Objects.requireNonNull(layoutData);
    }

    public static ScreenRenderedEvent create(String aggregateId, String screenId, DeviceType deviceType) {
        // Stubbed Layout generation logic based on device type
        Map<String, Object> layout = Map.of(
                "screenId", screenId,
                "device", deviceType.toString(),
                "format", "BMS_3270" // Simplified format logic
        );
        return new ScreenRenderedEvent(
                aggregateId,
                "screen.rendered",
                Instant.now(),
                layout
        );
    }
}
