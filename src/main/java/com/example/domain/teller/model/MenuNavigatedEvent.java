package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller successfully navigates to a new menu/screen.
 */
public record MenuNavigatedEvent(
        String aggregateId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        // Ensure immutability and basic validation
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "menu.navigated";
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
