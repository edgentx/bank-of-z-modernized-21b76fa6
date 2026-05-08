package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

/**
 * Event for S-19: MenuNavigatedEvent.
 * Emitted when a teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String previousMenuId,
    String targetMenuId,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "menu.navigated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
