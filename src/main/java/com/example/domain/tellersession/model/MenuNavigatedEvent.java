package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.menuId = menuId;
        this.action = action;
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
