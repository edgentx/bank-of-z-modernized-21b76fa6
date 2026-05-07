package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event emitted when a transaction (deposit or withdrawal) is posted.
 */
public record TransactionPostedEvent(
        String transactionId,
        String accountId,
        String kind, // "deposit" or "withdrawal"
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return kind + ".posted"; // Returns "deposit.posted" or "withdrawal.posted"
    }

    @Override
    public String aggregateId() {
        return transactionId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
