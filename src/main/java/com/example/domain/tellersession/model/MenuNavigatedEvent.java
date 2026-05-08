package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event emitted when a Teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String targetMenuId,
    String previousMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        // Ensure OccurredAt is never null for safety
        if (occurredAt == null) occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}