package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
    String aggregateId,
    String customerId,
    String accountNumber,
    String accountType,
    BigDecimal balance,
    String sortCode,
    Instant occurredAt
) implements DomainEvent {
    // The type() method is required by the interface
    @Override
    public String type() {
        return "account.opened";
    }
}