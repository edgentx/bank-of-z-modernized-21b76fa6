package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when an account status is updated.
 */
public record AccountStatusUpdatedEvent(
        String aggregateId,
        String oldStatus,
        String newStatus,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "account.status.updated";
    }
}
