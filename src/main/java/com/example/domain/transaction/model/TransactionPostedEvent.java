package com.example.domain.transaction.model;
import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
public record TransactionPostedEvent(String transactionId, String accountId, String kind, BigDecimal amount, String currency, Instant occurredAt) implements DomainEvent {
  @Override public String type() { return "transaction.posted"; }
  @Override public String aggregateId() { return transactionId; }
}
