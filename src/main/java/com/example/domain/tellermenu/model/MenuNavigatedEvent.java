package com.example.domain.tellermenu.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller successfully navigates to a new screen/menu.
 * Corresponds to Story S-19.
 */
public record MenuNavigatedEvent(
        String eventId,
        String aggregateId,
        String sessionId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
    }

    public MenuNavigatedEvent(String aggregateId, String sessionId, String menuId, String action, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, sessionId, menuId, action, occurredAt);
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
