package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Objects;

public class MenuNavigatedEvent implements DomainEvent {
    private final String aggregateId;
    private final String action;
    private final String previousMenuId;
    private final String currentMenuId;
    private final Instant occurredAt;

    public MenuNavigatedEvent(String aggregateId, String action, String previousMenuId, String currentMenuId, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.action = action;
        this.previousMenuId = previousMenuId;
        this.currentMenuId = currentMenuId;
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

    public String action() { return action; }
    public String previousMenuId() { return previousMenuId; }
    public String currentMenuId() { return currentMenuId; }
}