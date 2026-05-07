package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a teller successfully navigates to a new screen/menu.
 * This replaces the legacy 3270 data stream signal.
 */
public record MenuNavigatedEvent(
        String aggregateId,
        String sessionId,
        String menuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    public MenuNavigatedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(occurredAt);
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