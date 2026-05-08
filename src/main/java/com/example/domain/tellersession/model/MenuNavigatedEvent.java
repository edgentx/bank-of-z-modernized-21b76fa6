package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a Teller successfully navigates to a new menu.
 * Records the transition for audit trails and UI synchronization.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String previousMenuId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "menu.navigated";
    }
}
