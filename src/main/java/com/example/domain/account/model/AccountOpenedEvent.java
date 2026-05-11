package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
    String accountId,
    String customerId,
    String accountType,
    String sortCode,
    BigDecimal openingBalance,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "account.opened"; }
    @Override public String aggregateId() { return accountId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
