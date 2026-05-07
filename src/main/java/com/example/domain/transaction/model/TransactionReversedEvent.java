package com.example.domain.transaction.model;
import com.example.domain.shared.DomainEvent;
import java.time.Instant;
public record TransactionReversedEvent(String transactionId, String reason, Instant occurredAt) implements DomainEvent {
  @Override public String type() { return "transaction.reversed"; }
  @Override public String aggregateId() { return transactionId; }
}
