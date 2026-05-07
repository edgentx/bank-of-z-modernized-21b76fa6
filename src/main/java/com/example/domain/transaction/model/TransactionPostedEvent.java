package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

// This event represents the past state of the aggregate, used to reconstruct it for testing.
public record TransactionPostedEvent(
        String aggregateId,
        String accountId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "transaction.posted";
    }
}
