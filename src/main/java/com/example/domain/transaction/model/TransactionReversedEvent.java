package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionReversedEvent(
    String eventId,
    String aggregateId,
    String originalTransactionId,
    BigDecimal reversedAmount,
    Instant occurredAt
) implements DomainEvent {
  public TransactionReversedEvent {
    if (eventId == null) eventId = UUID.randomUUID().toString();
    if (occurredAt == null) occurredAt = Instant.now();
  }

  public TransactionReversedEvent(String aggregateId, String originalTransactionId, BigDecimal reversedAmount) {
    this(UUID.randomUUID().toString(), aggregateId, originalTransactionId, reversedAmount, Instant.now());
  }

  @Override
  public String type() {
    return "transaction.reversed";
  }

  @Override
  public Instant occurredAt() {
    return occurredAt;
  }
}
