package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
    String eventId,
    String aggregateId,
    String customerId,
    String accountType,
    BigDecimal initialBalance,
    String sortCode,
    String accountNumber, // Generated
    Instant occurredAt
) implements DomainEvent {
    public AccountOpenedEvent(String aggregateId, String customerId, String accountType, BigDecimal initialBalance, String sortCode) {
        this(
            UUID.randomUUID().toString(),
            aggregateId,
            customerId,
            accountType,
            initialBalance,
            sortCode,
            generateAccountNumber(),
            Instant.now()
        );
    }

    private static String generateAccountNumber() {
        // Simple mock generation for domain logic
        return "ACCT-" + System.currentTimeMillis();
    }

    @Override
    public String type() {
        return "account.opened";
    }
}
