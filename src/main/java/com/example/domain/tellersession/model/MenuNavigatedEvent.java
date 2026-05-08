package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a teller successfully navigates to a new menu context.
 */
public record MenuNavigatedEvent(
        String type,
        String aggregateId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(occurredAt);
    }

    public static MenuNavigatedEvent create(String aggregateId, String menuId, String action) {
        return new MenuNavigatedEvent(
                "menu.navigated",
                aggregateId,
                menuId,
                action,
                Instant.now()
        );
    }
}
