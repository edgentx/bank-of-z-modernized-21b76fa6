package com.example.domain;

import java.time.Instant;
import java.util.UUID;

public record DepositPostedEvent(
        UUID eventId,
        TransactionId transactionId,
        AccountNumber accountNumber,
        Money amount,
        Instant occurredOn
) implements DomainEvent {

    public DepositPostedEvent {
        if (eventId == null) eventId = UUID.randomUUID();
        if (occurredOn == null) occurredOn = Instant.now();
    }

    public static DepositPostedEvent create(TransactionId transactionId, AccountNumber accountNumber, Money amount) {
        return new DepositPostedEvent(UUID.randomUUID(), transactionId, accountNumber, amount, Instant.now());
    }

    @Override
    public String getEventType() {
        return "deposit.posted";
    }
}
