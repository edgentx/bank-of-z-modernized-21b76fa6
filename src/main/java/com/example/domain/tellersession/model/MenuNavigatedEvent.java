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
        // Ensure non-null for event storage safety
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}