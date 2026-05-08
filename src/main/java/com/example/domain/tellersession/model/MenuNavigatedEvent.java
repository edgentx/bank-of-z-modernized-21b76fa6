package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event for S-19: MenuNavigatedEvent.
 * Published when a teller successfully moves between screens.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String previousMenuId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
        // occurredAt defaults to now if null, though we usually pass it explicitly
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
