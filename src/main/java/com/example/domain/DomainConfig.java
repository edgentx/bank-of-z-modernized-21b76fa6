package com.example.domain;

import java.math.BigDecimal;

/**
 * Configuration object for the Transaction Aggregate.
 * Encapsulates limits and validation rules.
 */
public record DomainConfig(
    BigDecimal maxTransactionAmount,
    BigDecimal maxAccountBalance
) {
    public static DomainConfig defaults() {
        return new DomainConfig(
            new BigDecimal("1000000.00"), // 1M max transaction
            new BigDecimal("100000000.00") // 100M max balance
        );
    }
}
