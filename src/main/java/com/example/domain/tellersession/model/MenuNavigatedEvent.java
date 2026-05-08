package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a Teller successfully navigates to a new menu.
 */
public class MenuNavigatedEvent implements DomainEvent {

    private final String aggregateId;
    private final String fromMenuId;
    private final String toMenuId;
    private final String action;
    private final Instant occurredAt;

    public MenuNavigatedEvent(String aggregateId, String fromMenuId, String toMenuId, String action, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.fromMenuId = fromMenuId;
        this.toMenuId = toMenuId;
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

    public String fromMenuId() {
        return fromMenuId;
    }

    public String toMenuId() {
        return toMenuId;
    }

    public String action() {
        return action;
    }
}