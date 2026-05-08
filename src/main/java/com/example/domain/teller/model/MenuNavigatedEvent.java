package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when a Teller successfully navigates to a new screen/menu.
 * Used for audit trails and eventual state synchronization with legacy CICS.
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

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
