package com.example.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public sealed interface S11Event {
    UUID transactionId();
    Instant timestamp();

    record WithdrawalPosted(
            UUID transactionId,
            String accountNumber,
            BigDecimal amount,
            String currency,
            BigDecimal balanceAfter,
            Instant timestamp
    ) implements S11Event {}

    record TransactionRejected(
            UUID transactionId,
            String reason,
            Instant timestamp
    ) implements S11Event {}
}
