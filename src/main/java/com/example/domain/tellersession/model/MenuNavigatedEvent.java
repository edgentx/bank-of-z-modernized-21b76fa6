package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller successfully navigates to a new menu context.
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

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}