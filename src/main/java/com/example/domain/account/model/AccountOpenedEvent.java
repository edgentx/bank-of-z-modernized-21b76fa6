package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
    String aggregateId,
    String customerId,
    String accountType,
    BigDecimal initialDeposit,
    String sortCode,
    String accountNumber,
    Instant occurredAt
) implements DomainEvent {
    public AccountOpenedEvent(String aggregateId, String customerId, String accountType, BigDecimal initialDeposit, String sortCode, String accountNumber, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.customerId = customerId;
        this.accountType = accountType;
        this.initialDeposit = initialDeposit;
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "account.opened";
    }
}
