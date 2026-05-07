package com.example.domain.transaction.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WithdrawalPostedEvent(
    String aggregateId,
    String accountNumber,
    BigDecimal amount,
    String currency,
    Instant occurredAt
) implements DomainEvent {
    public WithdrawalPostedEvent {
        // Ensure aggregateId is non-null if constructed manually
    }
    
    @Override
    public String type() {
        return "withdrawal.posted";
    }
    
    // Convenience constructor for testing
    public static WithdrawalPostedEvent create(String transactionId, String accountNumber, BigDecimal amount, String currency) {
        return new WithdrawalPostedEvent(transactionId, accountNumber, amount, currency, Instant.now());
    }
}