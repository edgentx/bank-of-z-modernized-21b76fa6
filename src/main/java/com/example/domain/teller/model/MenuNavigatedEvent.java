package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        // Ensure defaults for safety if needed, though records handle nulls via constructors
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    // Factory method to match existing patterns if necessary, or simple constructor usage
    public static MenuNavigatedEvent create(String sessionId, String menuId, String action) {
        return new MenuNavigatedEvent(sessionId, menuId, action, Instant.now());
    }
}