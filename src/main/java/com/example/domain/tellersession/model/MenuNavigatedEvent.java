package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller successfully navigates to a new menu context.
 * S-19: user-interface-navigation.
 */
public record MenuNavigatedEvent(
    String eventId,
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, menuId, action, occurredAt);
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
