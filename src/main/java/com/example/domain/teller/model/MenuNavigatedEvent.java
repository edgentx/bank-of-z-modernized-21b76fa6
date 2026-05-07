package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a Teller successfully navigates to a new menu/screen.
 */
public record MenuNavigatedEvent(
        String aggregateId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        // Ensure Java Records null safety if desired, though primitive checks are often enough
    }

    public MenuNavigatedEvent(String aggregateId, String menuId, String action) {
        this(aggregateId, menuId, action, Instant.now());
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
