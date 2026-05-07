package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller successfully navigates to a new screen.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        // Ensure basic validation
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
