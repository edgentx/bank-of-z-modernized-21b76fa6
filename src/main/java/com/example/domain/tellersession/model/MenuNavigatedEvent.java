package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String sourceMenuId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(sourceMenuId, "sourceMenuId required");
        Objects.requireNonNull(targetMenuId, "targetMenuId required");
        Objects.requireNonNull(action, "action required");
        Objects.requireNonNull(occurredAt, "occurredAt required");
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
