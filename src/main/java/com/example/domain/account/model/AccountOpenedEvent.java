package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
    String aggregateId,
    String accountNumber,
    String customerId,
    String accountType,
    BigDecimal balance,
    String sortCode,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() { return "account.opened"; }
}
