package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DepositPostedEvent(
        String aggregateId,
        String accountNumber,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "deposit.posted";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
