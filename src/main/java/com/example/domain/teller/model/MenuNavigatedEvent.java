package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when the teller successfully navigates to a new menu context.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String menuId,
    String action,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }
}
