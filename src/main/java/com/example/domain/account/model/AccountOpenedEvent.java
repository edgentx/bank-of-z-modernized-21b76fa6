package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when an account is successfully opened.
 */
public record AccountOpenedEvent(
        String aggregateId,
        String customerId,
        String accountType,
        String sortCode,
        BigDecimal initialBalance,
        Instant occurredAt
) implements DomainEvent {

    public AccountOpenedEvent {
        // Ensure type() method is consistent, though record constructor handles fields.
        // We can add validation here if needed.
    }

    @Override
    public String type() {
        return "account.opened";
    }
}
