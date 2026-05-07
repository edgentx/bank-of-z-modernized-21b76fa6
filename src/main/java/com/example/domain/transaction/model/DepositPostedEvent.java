package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class DepositPostedEvent implements DomainEvent {

    private final String aggregateId;
    private final String accountNumber;
    private final BigDecimal amount;
    private final String currency;
    private final Instant occurredAt;

    public DepositPostedEvent(
        String aggregateId,
        String accountNumber,
        BigDecimal amount,
        String currency,
        Instant occurredAt
    ) {
        this.aggregateId = aggregateId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = occurredAt;
    }

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

    public String accountNumber() { return accountNumber; }
    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }
}
