package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller successfully navigates to a new menu context.
 */
public class MenuNavigatedEvent implements DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final String menuId;
    private final String action;
    private final Instant occurredAt;

    public MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.menuId = menuId;
        this.action = action;
        this.occurredAt = occurredAt;
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

    public String eventId() { return eventId; }
    public String getMenuId() { return menuId; }
    public String getAction() { return action; }
}
