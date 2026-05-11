package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller successfully navigates to a new menu context.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.menuId = menuId;
        this.action = action;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    // Explicit no-arg constructor for serialization/mapping frameworks if needed,
    // though record constructors are preferred. This placeholder ensures compatibility.
    public static MenuNavigatedEvent create(String aggregateId, String menuId, String action, Instant occurredAt) {
        return new MenuNavigatedEvent(aggregateId, menuId, action, occurredAt);
    }
}
