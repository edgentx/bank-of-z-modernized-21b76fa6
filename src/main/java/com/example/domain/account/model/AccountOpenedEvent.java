package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event emitted when a new account is successfully opened.
 */
public record AccountOpenedEvent(
    String aggregateId,
    String customerId,
    String accountNumber,
    String accountType,
    BigDecimal balance,
    String sortCode,
    String status,
    Instant occurredAt
) implements DomainEvent {

  @Override
  public String type() {
    return "account.opened";
  }

  @Override
  public String aggregateId() {
    return aggregateId;
  }

  @Override
  public Instant occurredAt() {
    return occurredAt;
  }
}
