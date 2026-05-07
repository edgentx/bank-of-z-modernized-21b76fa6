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
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        Instant occurredAt
) implements DomainEvent {

    public TransferInitiatedEvent(String aggregateId, String fromAccountId, String toAccountId, BigDecimal amount, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, fromAccountId, toAccountId, amount, occurredAt);
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }
}
