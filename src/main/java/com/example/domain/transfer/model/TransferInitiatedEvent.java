package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a transfer is initiated.
 */
public record TransferInitiatedEvent(
    String eventId,
    String aggregateId,
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    String currency,
    Instant occurredAt
) implements DomainEvent {
    public TransferInitiatedEvent(String aggregateId, String fromAccount, String toAccount, BigDecimal amount, String currency, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, fromAccount, toAccount, amount, currency, occurredAt);
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }
}
