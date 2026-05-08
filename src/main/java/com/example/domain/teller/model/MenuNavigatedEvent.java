package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a teller successfully navigates to a new menu/screen.
 */
public record MenuNavigatedEvent(
        String aggregateId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(menuId);
        Objects.requireNonNull(action);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}