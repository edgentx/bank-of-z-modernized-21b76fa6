package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a screen layout is successfully generated.
 * Used by S-21: ScreenMap RenderScreenCmd.
 */
public record ScreenRenderedEvent(
    String type,
    String aggregateId,
    Instant occurredAt,
    String screenId,
    String deviceType,
    String layout
) implements DomainEvent {
    public ScreenRenderedEvent(String aggregateId, String screenId, String deviceType, String layout) {
        this("screen.rendered", aggregateId, Instant.now(), screenId, deviceType, layout);
    }
}
