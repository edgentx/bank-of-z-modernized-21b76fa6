package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a teller successfully navigates to a new menu context.
 */
public record MenuNavigatedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String menuId,
        String action
) implements DomainEvent {

    public MenuNavigatedEvent(String aggregateId, String menuId, String action) {
        this("menu.navigated", aggregateId, Instant.now(), menuId, action);
    }

    @Override
    public String type() {
        return type;
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
