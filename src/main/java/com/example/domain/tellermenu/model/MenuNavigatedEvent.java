package com.example.domain.tellermenu.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MenuNavigatedEvent(
    String eventId,
    String aggregateId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        if (eventId == null || eventId.isBlank()) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    // Constructor optimized for the Aggregate to use easily
    public MenuNavigatedEvent(String aggregateId, String targetMenuId, String action) {
        this(UUID.randomUUID().toString(), aggregateId, targetMenuId, action, Instant.now());
    }
}
