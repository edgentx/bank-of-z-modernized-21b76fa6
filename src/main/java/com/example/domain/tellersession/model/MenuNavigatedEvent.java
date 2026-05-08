package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event emitted when a Teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
        String aggregateId,
        String tellerId,
        String targetMenuId,
        String action,
        Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}