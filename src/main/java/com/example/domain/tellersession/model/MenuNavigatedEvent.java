package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String tellerId,
    String previousMenuId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        // Defensive null checks if needed, though records imply validation in constructor or via Aggregate
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId required");
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}