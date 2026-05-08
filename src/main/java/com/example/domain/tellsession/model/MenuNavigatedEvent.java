package com.example.domain.tellsession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a Teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String eventId,
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, menuId, action, occurredAt);
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
