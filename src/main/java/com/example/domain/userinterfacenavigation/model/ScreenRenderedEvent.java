package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a screen has been successfully rendered.
 */
public record ScreenRenderedEvent(
    String aggregateId,
    String deviceType,
    Instant occurredAt
) implements DomainEvent {

    public ScreenRenderedEvent {
        // Ensure aggregateId is not null in the record
        if (aggregateId == null) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
    }

    @Override
    public String type() {
        return "screen.rendered";
    }

    // DomainEvent interface expects aggregateId(), which is provided by the record component.
    // occurredAt() is provided by the record component.
}
