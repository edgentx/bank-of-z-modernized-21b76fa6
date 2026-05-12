package com.example.domain.account.model;
import com.example.domain.shared.DomainEvent;
import java.time.Instant;
public record AccountOpenedEvent(String accountId, String customerId, String accountType, long initialDeposit, String sortCode, Instant occurredAt) implements DomainEvent {
  @Override public String type() { return "account.opened"; }
  @Override public String aggregateId() { return accountId; }
}
