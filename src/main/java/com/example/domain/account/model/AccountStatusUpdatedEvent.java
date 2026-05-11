package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when an account status is updated.
 */
public record AccountStatusUpdatedEvent(
        String aggregateId,
        String accountNumber,
        AccountStatus newStatus,
        Instant occurredAt
) implements DomainEvent {

    public AccountStatusUpdatedEvent {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber cannot be null");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus cannot be null");
        }
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
    }

    @Override
    public String eventType() {
        return "account.status.updated";
    }
}
