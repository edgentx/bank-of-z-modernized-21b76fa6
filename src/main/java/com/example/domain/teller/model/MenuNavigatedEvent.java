package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller successfully navigates to a new menu context.
 * S-19: user-interface-navigation.
 */
public class MenuNavigatedEvent implements DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final String sessionId;
    private final String menuId;
    private final String action;
    private final Instant occurredAt;

    public MenuNavigatedEvent(String aggregateId, String sessionId, String menuId, String action, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.sessionId = sessionId;
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

    public String sessionId() { return sessionId; }
    public String menuId() { return menuId; }
    public String action() { return action; }
}
