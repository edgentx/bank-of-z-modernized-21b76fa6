package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a Teller successfully navigates to a new menu.
 * Story S-19.
 */
public record MenuNavigatedEvent(String aggregateId, String menuId, String action, Instant occurredAt) implements DomainEvent {
    public MenuNavigatedEvent {
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override public String type() { return "menu.navigated"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
