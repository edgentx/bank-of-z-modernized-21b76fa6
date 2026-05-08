package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountOpenedEvent(
    String aggregateId,
    String customerId,
    AccountAggregate.AccountType accountType,
    BigDecimal balance,
    String sortCode,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "account.opened";
    }

    // Override aggregateId to match record field name if necessary, or map in constructor.
    // The DomainEvent interface expects aggregateId().
}