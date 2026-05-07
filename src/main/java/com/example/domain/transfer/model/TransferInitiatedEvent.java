package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Event emitted when a transfer is initiated.
 */
public class TransferInitiatedEvent implements DomainEvent {

    private final String eventId;
    private final String aggregateId;
    private final String fromAccountId;
    private final String toAccountId;
    private final BigDecimal amount;
    private final String currency;
    private final Instant occurredAt;

    public TransferInitiatedEvent(
            String aggregateId,
            String fromAccountId,
            String toAccountId,
            BigDecimal amount,
            String currency,
            Instant occurredAt
    ) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        this.fromAccountId = Objects.requireNonNull(fromAccountId, "fromAccountId cannot be null");
        this.toAccountId = Objects.requireNonNull(toAccountId, "toAccountId cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        this.currency = Objects.requireNonNull(currency, "currency cannot be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
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

    public String eventId() {
        return eventId;
    }

    public String fromAccountId() {
        return fromAccountId;
    }

    public String toAccountId() {
        return toAccountId;
    }

    public BigDecimal amount() {
        return amount;
    }

    public String currency() {
        return currency;
    }
}
