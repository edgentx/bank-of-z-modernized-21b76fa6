package com.example.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public record DepositPostedEvent(
        String eventId,
        String transactionId,
        String accountNumber,
        BigDecimal amount,
        Currency currency,
        String type
) {
    public DepositPostedEvent {
        type = "deposit.posted";
    }

    public static DepositPostedEvent create(String transactionId, PostDepositCmd cmd) {
        return new DepositPostedEvent(
                UUID.randomUUID().toString(),
                transactionId,
                cmd.accountNumber(),
                cmd.amount(),
                cmd.currency(),
                "deposit.posted"
        );
    }
}
