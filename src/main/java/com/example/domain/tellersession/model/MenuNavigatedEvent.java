package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a menu navigation is successfully performed.
 * Part of Story S-19: TellerSession user-interface-navigation.
 */
public class MenuNavigatedEvent implements DomainEvent {
    private final String aggregateId;
    private final String menuId;
    private final String action;
    private final Instant occurredAt;

    public MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) {
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

    public String menuId() { return menuId; }
    public String action() { return action; }
}
