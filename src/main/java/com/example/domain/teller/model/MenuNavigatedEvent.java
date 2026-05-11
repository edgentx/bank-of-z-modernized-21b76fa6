package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a menu navigation is successful.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String previousMenuId,
    String action,
    String nextMenuId,
    Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        // Ensure defaults if null
        if (occurredAt == null) occurredAt = Instant.now();
    }
    @Override public String type() { return "menu.navigated"; }
}