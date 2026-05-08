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
        // Ensure non-null for sanity, though constructor validation handles logic
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    // Factory to match existing aggregate style
    public static MenuNavigatedEvent create(String aggregateId, String menuId, String action) {
        return new MenuNavigatedEvent(aggregateId, menuId, action, Instant.now());
    }
}
