package com.example.domain.tellersession.model;

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
        // Ensure null safety
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId cannot be null");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
