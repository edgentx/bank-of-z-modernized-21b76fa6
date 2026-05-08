package com.example.domain.screen.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a screen map is successfully rendered.
 */
public record ScreenRenderedEvent(
        String aggregateId,
        String deviceType,
        Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        // Validate non-null after construction for safety
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be blank");
        }
    }

    @Override
    public String type() {
        return "screen.rendered";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
