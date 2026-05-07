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
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId cannot be null");
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be null");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId cannot be null");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action cannot be null");
        if (occurredAt == null) throw new IllegalArgumentException("occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}