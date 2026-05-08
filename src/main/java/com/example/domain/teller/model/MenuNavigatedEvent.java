package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String aggregateId,
    String sessionId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        // Validate constructor parameters
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId cannot be null");
    }

    public MenuNavigatedEvent(String sessionId, String menuId, String action, Instant occurredAt) {
        this(UUID.randomUUID().toString(), sessionId, menuId, action, occurredAt);
    }

    @Override public String type() { return "menu.navigated"; }

    @Override public String aggregateId() { return aggregateId; }

    @Override public Instant occurredAt() { return occurredAt; }
}
