package com.example.domain.tellermiddleware.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a Teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
        String aggregateId,
        String targetMenuId,
        String action,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }
}
