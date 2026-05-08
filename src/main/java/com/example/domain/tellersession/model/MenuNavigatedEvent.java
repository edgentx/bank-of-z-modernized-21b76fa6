package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a teller successfully navigates the menu system.
 * S-19.
 */
public record MenuNavigatedEvent(String aggregateId, String targetMenuId, String action, Instant occurredAt) implements DomainEvent {
    public MenuNavigatedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(targetMenuId);
        Objects.requireNonNull(action);
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
}
