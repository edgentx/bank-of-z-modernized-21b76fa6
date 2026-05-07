package com.example.domain.transfer.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferInitiatedEvent(
    String eventId,
    String aggregateId,
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    Instant occurredAt
) implements DomainEvent {
    public TransferInitiatedEvent {
        if (eventId == null) eventId = UUID.randomUUID().toString();
    }

    public TransferInitiatedEvent(String aggregateId, String fromAccount, String toAccount, BigDecimal amount, Instant occurredAt) {
        this(UUID.randomUUID().toString(), aggregateId, fromAccount, toAccount, amount, occurredAt);
    }

    @Override
    public String type() {
        return "transfer.initiated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
