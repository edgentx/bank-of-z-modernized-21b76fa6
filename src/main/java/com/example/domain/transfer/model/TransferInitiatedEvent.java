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
        String currency,
        Instant occurredAt
) implements DomainEvent {

    public TransferInitiatedEvent {
        // Ensure valid state
        if (eventId == null || eventId.isBlank()) {
            eventId = UUID.randomUUID().toString();
        }
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(fromAccountId, "fromAccountId required");
        Objects.requireNonNull(toAccountId, "toAccountId required");
        Objects.requireNonNull(amount, "amount required");
        Objects.requireNonNull(currency, "currency required");
        Objects.requireNonNull(occurredAt, "occurredAt required");
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }
}
