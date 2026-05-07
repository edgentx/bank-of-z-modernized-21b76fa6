package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a teller successfully navigates to a menu.
 */
public record MenuNavigatedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String sessionId,
        String menuId,
        String action
) implements DomainEvent {

    public MenuNavigatedEvent {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    public MenuNavigatedEvent(String sessionId, String menuId, String action, Instant occurredAt) {
        this("menu.navigated", sessionId, occurredAt, sessionId, menuId, action);
    }

    @Override
    public String type() {
        return type;
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
