package com.example.domain.aggregator.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
        String eventId,
        String aggregateId,
        String targetMenuId,
        String action,
        Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (targetMenuId == null || targetMenuId.isBlank()) throw new IllegalArgumentException("targetMenuId required");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action required");
    }

    public MenuNavigatedEvent(String aggregateId, String targetMenuId, String action, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, targetMenuId, action, occurredAt);
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