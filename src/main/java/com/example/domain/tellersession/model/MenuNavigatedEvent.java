package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

public class MenuNavigatedEvent implements DomainEvent {
    private final String sessionId;
    private final String menuId;
    private final String action;
    private final Instant occurredAt;

    public MenuNavigatedEvent(String sessionId, String menuId, String action, Instant occurredAt) {
        this.sessionId = Objects.requireNonNull(sessionId);
        this.menuId = Objects.requireNonNull(menuId);
        this.action = Objects.requireNonNull(action);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public String aggregateId() {
        return sessionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String menuId() { return menuId; }
    public String action() { return action; }
}
