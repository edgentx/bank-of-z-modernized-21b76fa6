package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a Teller successfully navigates to a new screen.
 */
public record MenuNavigatedEvent(
        String aggregateId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}