package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferCompletedEvent(
    String event_id,
    String transferId,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount,
    Instant occurredAt
) implements DomainEvent {
    public TransferCompletedEvent(String transferId, String fromAccountId, String toAccountId, BigDecimal amount, Instant occurredAt) {
        this(UUID.randomUUID().toString(), transferId, fromAccountId, toAccountId, amount, occurredAt);
    }
    @Override public String type() { return "transfer.completed"; }
    @Override public String aggregateId() { return transferId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
