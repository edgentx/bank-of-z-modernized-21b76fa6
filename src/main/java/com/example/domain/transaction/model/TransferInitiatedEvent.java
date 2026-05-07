package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a transfer is successfully initiated.
 * Part of S-13: Implement InitiateTransferCmd on Transfer.
 */
public class TransferInitiatedEvent implements DomainEvent {

    private final String eventId;
    private final String aggregateId;
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;
    private final String currency;
    private final Instant occurredAt;

    public TransferInitiatedEvent(
            String transferId,
            String fromAccount,
            String toAccount,
            BigDecimal amount,
            String currency,
            Instant occurredAt
    ) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = transferId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.currency = currency;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}