package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String targetMenuId,
    String actionTaken,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        // Defensive defaults if necessary, though records enforce initialization
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
}
