package com.example.domain.statement.model;
import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;
public record StatementGeneratedEvent(String statementId, String accountNumber, LocalDate periodEnd, Instant occurredAt) implements DomainEvent {
  @Override public String type() { return "statement.generated"; }
  @Override public String aggregateId() { return statementId; }
}
