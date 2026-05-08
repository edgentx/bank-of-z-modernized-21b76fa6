package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller successfully navigates to a new screen.
 */
public record MenuNavigatedEvent(
        String aggregateId,
        String previousMenuId,
        String currentMenuId,
        String action,
        Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        // Validation defaults if necessary, though records are strict
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}