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
        // Ensure immutability and valid data
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (menuId == null) throw new IllegalArgumentException("menuId required");
    }

    public MenuNavigatedEvent(String aggregateId, String menuId, String action) {
        this(aggregateId, menuId, action, Instant.now());
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}