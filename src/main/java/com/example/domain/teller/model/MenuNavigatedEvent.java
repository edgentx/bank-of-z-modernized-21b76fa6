package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class MenuNavigatedEvent implements DomainEvent {
    private final String eventId;
    private final String sessionId;
    private final String previousMenuId;
    private final String targetMenuId;
    private final String action;
    private final Instant occurredAt;

    public MenuNavigatedEvent(String sessionId, String previousMenuId, String targetMenuId, String action, Instant occurredAt) {
        this.eventId = UUID.randomUUID().toString();
        this.sessionId = sessionId;
        this.previousMenuId = previousMenuId;
        this.targetMenuId = targetMenuId;
        this.action = action;
        this.occurredAt = occurredAt;
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

    public String eventId() { return eventId; }
    public String sessionId() { return sessionId; }
    public String previousMenuId() { return previousMenuId; }
    public String targetMenuId() { return targetMenuId; }
    public String action() { return action; }
}
