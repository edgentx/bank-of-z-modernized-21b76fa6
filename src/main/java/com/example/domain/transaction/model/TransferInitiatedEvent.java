package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event emitted when a transfer is successfully initiated.
 */
public record TransferInitiatedEvent(
        String eventId,
        String aggregateId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) implements DomainEvent {

    public TransferInitiatedEvent {
        // Ensure IDs are valid
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null");
        }
    }

    public TransferInitiatedEvent(String aggregateId, String fromAccountId, String toAccountId, BigDecimal amount, String currency, Instant occurredAt) {
        this(
                UUID.randomUUID().toString(),
                aggregateId,
                fromAccountId,
                toAccountId,
                amount,
                currency,
                occurredAt
        );
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }
}
