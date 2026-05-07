package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when the teller successfully navigates to a new menu.
 */
public record MenuNavigatedEvent(
    String aggregateId,
    String targetMenuId,
    String action,
    Instant occurredAt
) implements DomainEvent {

    public MenuNavigatedEvent(String aggregateId, String targetMenuId, String action, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.targetMenuId = targetMenuId;
        this.action = action;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "menu.navigated";
    }
}
