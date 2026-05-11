package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
    String aggregateId,
    String customerId,
    AccountType accountType,
    BigDecimal initialDeposit,
    String sortCode,
    String generatedAccountNumber,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "account.opened";
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
