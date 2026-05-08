package com.example.domain.uinavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a screen map is successfully rendered.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String deviceType,
    int width,
    int height,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "screen.rendered"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
