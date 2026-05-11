package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a Teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        // Ensure ID is present if not provided via constructor defaults
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
