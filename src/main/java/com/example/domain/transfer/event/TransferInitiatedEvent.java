package com.example.domain.transfer.event;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferInitiatedEvent(
    String aggregateId,
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    Instant occurredAt
) implements DomainEvent {
    public TransferInitiatedEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(fromAccount);
        Objects.requireNonNull(toAccount);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }
}
