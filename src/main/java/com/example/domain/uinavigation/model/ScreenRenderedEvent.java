package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a screen map is successfully rendered.
 * Contains the generated layout (JSON) and metadata.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String deviceType,
    String layout,
    Instant occurredAt
) implements DomainEvent {

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
}
