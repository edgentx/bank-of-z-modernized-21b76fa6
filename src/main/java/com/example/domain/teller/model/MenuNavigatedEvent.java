package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a Teller successfully navigates to a new menu context.
 * Part of S-19 user-interface-navigation.
 */
public record MenuNavigatedEvent(
        String aggregateId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    public MenuNavigatedEvent(String aggregateId, String menuId, String action) {
        this(aggregateId, menuId, action, Instant.now());
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
