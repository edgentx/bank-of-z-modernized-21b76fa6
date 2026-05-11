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
        // Ensure non-null for domain integrity
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId cannot be null");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId cannot be null");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action cannot be null");
    }

    public MenuNavigatedEvent(String aggregateId, String menuId, String action) {
        this(aggregateId, menuId, action, Instant.now());
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}