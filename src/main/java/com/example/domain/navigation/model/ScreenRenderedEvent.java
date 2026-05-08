package com.example.domain.navigation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

/**
 * Event emitted when a screen layout is successfully generated.
 * Stores the layout definition (Map of fields/attributes) and metadata.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String screenId,
    String deviceType,
    Map<String, String> layoutAttributes,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "screen.rendered";
    }
}
