package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
        String aggregateId,
        String customerId,
        String accountType,
        BigDecimal balance,
        String sortCode,
        String accountNumber, // Immutable
        Instant occurredAt
) implements DomainEvent {
    public AccountOpenedEvent {
        // Ensure occurredAt is set if not provided
        if (occurredAt == null) occurredAt = Instant.now();
        if (accountNumber == null) accountNumber = UUID.randomUUID().toString();
    }

    @Override
    public String type() {
        return "account.opened";
    }
}
