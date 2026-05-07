package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionPostedEvent(
        String transactionId,
        String accountId,
        String kind,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        // Per S-10 AC and review feedback, we emit specific types like 'deposit.posted'.
        // This is an intentional contract change to support specific event handling.
        return kind + ".posted";
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
